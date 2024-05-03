package me.dio.hiokdev.reactiveflashcards.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.Card;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyCard;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.DeckInStudyException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.StudyDomainMapper;
import me.dio.hiokdev.reactiveflashcards.domain.repository.StudyRepository;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Objects;
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
                .map(study -> {
                    // TODO()
                    return study;
                });
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
        currentQuestion = currentQuestion.toBuilder().answered(answer).build();
        var currentQuestionIndex = questions.indexOf(currentQuestion);
        questions.set(currentQuestionIndex, currentQuestion);
        return document.toBuilder().questions(questions).build();
    }

    private Flux<StudyCard> getIncorrectAnsweredOrUnansweredStudyCards(final StudyDocument studyDocument) {
        return Flux.fromIterable(studyDocument.studyDeck().cards())
                .filter(card -> studyDocument.questions().stream()
                        .filter(Question::isCorrect)
                        .map(Question::asked)
                        .anyMatch(questionAsk -> !card.front().equals(questionAsk)));
    }

}
