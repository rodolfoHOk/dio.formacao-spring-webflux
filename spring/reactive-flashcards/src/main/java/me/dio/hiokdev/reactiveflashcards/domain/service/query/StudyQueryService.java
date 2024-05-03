package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.repository.StudyRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyQueryService {

    private final StudyRepository studyRepository;

    public Mono<StudyDocument> findPendingStudyByUserIdAndDeckId(final String userId, final String deckId) {
        return studyRepository.findByUserIdAndStudyDeck_DeckIdAndCompletedFalse(userId, deckId)
                .doFirst(() -> log.info("===== Try to get pending study with userId {} and deckId {}", userId, deckId))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .PENDING_STUDY_NOT_FOUND.params(userId, deckId).getMessage()))));
    }

    public Flux<StudyDocument> findAllByUserId(final String userId) {
        return studyRepository.findAllByUserId(userId)
                .doFirst(() -> log.info("==== Try to find studies with user id {}", userId));
    }

    public Mono<StudyDocument> findById(final String id) {
        return studyRepository.findById(id)
                .doFirst(() -> log.info("==== Getting a study with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .STUDY_NOT_FOUND.params(id).getMessage()))));
    }

    public Mono<StudyDocument> verifyIfFinished(final StudyDocument studyDocument){
        return Mono.just(studyDocument)
                .doFirst(() -> log.info("==== verify if study has some question without right answer"))
                .filter(study -> BooleanUtils.isFalse(studyDocument.completed()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .STUDY_QUESTION_NOT_FOUND.params(studyDocument.id()).getMessage()))));
    }

    public Mono<Question> getLastPendingQuestion(final String id) {
        return findById(id)
                .flatMap(this::verifyIfFinished)
                .flatMapMany(study -> Flux.fromIterable(study.questions()))
                .filter(Question::isNotAnswered)
                .doFirst(() -> log.info("==== Getting a current pending question in study {}", id))
                .single();
    }

}
