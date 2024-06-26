package me.dio.hiokdev.reactiveflashcards.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.Card;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyCard;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.QuestionDTO;
import me.dio.hiokdev.reactiveflashcards.domain.dto.StudyDTO;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.DeckInStudyException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapper;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.StudyDomainMapper;
import me.dio.hiokdev.reactiveflashcards.domain.repository.StudyRepository;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyService {

    private final UserQueryService userQueryService;
    private final DeckQueryService deckQueryService;
    private final StudyQueryService studyQueryService;
    private final StudyDomainMapper studyDomainMapper;
    private final StudyRepository studyRepository;
    private final MailService mailService;
    private final MailMapper mailMapper;

    public Mono<StudyDocument> start(final StudyDocument studyDocument) {
        return verifyPendingStudy(studyDocument)
                .then(Mono.defer(() -> userQueryService.findById(studyDocument.userId())))
                .flatMap(user -> deckQueryService.findById(studyDocument.studyDeck().deckId()))
                .flatMap(deck -> fillDeckStudyCards(studyDocument, deck.cards()))
                .map(study -> study.toBuilder()
                        .question(generateRandomQuestion(study.studyDeck().cards()))
                        .build())
                .flatMap(studyRepository::save)
                .doOnSuccess(study -> log.info("A follow study was save {}", study));
    }

    public Mono<StudyDocument> answer(final String id, final String answer) {
        return studyQueryService.findById(id)
                .flatMap(studyQueryService::verifyIfFinished)
                .map(study -> addAnswerToCurrentQuestion(study, answer))
                .zipWhen(this::getNextPossibilities)
                .map(tuple -> studyDomainMapper.toDTO(tuple.getT1(), tuple.getT2()))
                .flatMap(this::setNewQuestion)
                .map(studyDomainMapper::toDocument)
                .flatMap(studyRepository::save)
                .doFirst(() -> log.info("==== Saving answer and next question if have one"));
    }

    private Mono<Void> verifyPendingStudy(final StudyDocument studyDocument) {
        return studyQueryService
                .findPendingStudyByUserIdAndDeckId(studyDocument.userId(), studyDocument.studyDeck().deckId())
                .flatMap(study -> Mono.defer(() -> Mono.error(new DeckInStudyException(BaseErrorMessage
                        .DECK_IN_STUDY.params(studyDocument.userId(), studyDocument.studyDeck().deckId())
                        .getMessage()))))
                .onErrorResume(NotFoundException.class, e -> Mono.empty())
                .then();
    }

    private Mono<StudyDocument> fillDeckStudyCards(final StudyDocument studyDocument, final Set<Card> cards) {
        return Flux.fromIterable(cards)
                .doFirst(() -> log.info("==== Copy cards to new study"))
                .map(studyDomainMapper::toStudyCard)
                .collectList()
                .map(studyCards -> studyDocument.studyDeck().toBuilder().cards(Set.copyOf(studyCards)).build())
                .map(studyDeck -> studyDocument.toBuilder().studyDeck(studyDeck).build());
    }

    private Question generateRandomQuestion(final Set<StudyCard> studyCards) {
        log.info("==== Generating a random question");
        var cards = new ArrayList<>(studyCards);
        var random = new Random();
        var position = random.nextInt(cards.size());
        return studyDomainMapper.toQuestion(cards.get(position));
    }

    private StudyDocument addAnswerToCurrentQuestion(final StudyDocument document, final String answer) {
        var currentQuestion = document.getLastPendingQuestion();
        var questions = document.questions();
        var currentQuestionIndex = questions.indexOf(currentQuestion);
        currentQuestion = currentQuestion.toBuilder().answered(answer).build();
        questions.set(currentQuestionIndex, currentQuestion);
        return document.toBuilder().questions(questions).build();
    }

    private Mono<List<String>> getNextPossibilities(final StudyDocument studyDocument) {
        return Flux.fromIterable(studyDocument.studyDeck().cards())
                .doFirst(() -> log.info("==== Getting question not used or questions without right answers"))
                .map(StudyCard::front)
                .filter(cardAsk -> studyDocument.questions().stream()
                        .filter(Question::isCorrect)
                        .map(Question::asked)
                        .noneMatch(cardAsk::equals))
                .collectList()
                .flatMap(cardsAsks -> removeLastAsk(cardsAsks, studyDocument.getLastAnsweredQuestion().asked()));
    }

    private Mono<List<String>> removeLastAsk(List<String> cardsAsks, final String lastAsked) {
        return Mono.just(cardsAsks)
                .doFirst(() -> log.info("==== Remove last asked question if it is not a last pending question in study"))
                .filter(asks -> asks.size() == 1)
                .switchIfEmpty(Mono.defer(() -> Mono.just(cardsAsks.stream()
                        .filter(ask -> !ask.equals(lastAsked))
                        .toList())));
    }

    private Mono<StudyDTO> setNewQuestion(final StudyDTO studyDTO) {
        return Mono.just(studyDTO.hasAnyAnswer())
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .STUDY_QUESTION_NOT_FOUND.params(studyDTO.id()).getMessage()))))
                .flatMap(hasAnyAnswer -> generateNextQuestion(studyDTO))
                .map(question -> studyDTO.toBuilder().question(question).build())
                .onErrorResume(NotFoundException.class, e -> Mono.just(studyDTO)
                        .onTerminateDetach()
                        .doOnSuccess(this::notifyUser));
    }

    private Mono<QuestionDTO> generateNextQuestion(final StudyDTO studyDTO) {
        return Mono.just(studyDTO.remainAsks().get(new Random().nextInt(studyDTO.remainAsks().size())))
                .doFirst(() -> log.info("==== Select next random question"))
                .map(ask -> studyDTO.studyDeck().cards().stream()
                        .filter(card -> ask.equals(card.front()))
                        .map(studyDomainMapper::toDTO)
                        .findFirst().orElseThrow());
    }

    private void notifyUser(final StudyDTO dto) {
        userQueryService.findById(dto.userId())
                .zipWhen(user -> deckQueryService.findById(dto.studyDeck().deckId()))
                .map(tuple -> mailMapper.toDTO(dto, tuple.getT2(), tuple.getT1()))
                .flatMap(mailService::send)
                .subscribe();
    }

}
