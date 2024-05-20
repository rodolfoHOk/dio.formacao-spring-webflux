package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.repository.StudyRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class StudyQueryServiceTest {

    @Mock
    private StudyRepository studyRepository;
    private StudyQueryService studyQueryService;

    @BeforeEach
    void setup() {
        this.studyQueryService = new StudyQueryService(studyRepository);
    }

    @Test
    void findPendingStudyByUserIdAndDeckIdTest() {
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(ObjectId.get().toString(), deck).build();
        when(studyRepository.findByUserIdAndStudyDeck_DeckIdAndCompletedFalse(anyString(), anyString()))
                .thenReturn(Mono.just(study));

        StepVerifier.create(studyQueryService
                        .findPendingStudyByUserIdAndDeckId(study.userId(), study.studyDeck().deckId()))
                .assertNext(actual -> assertThat(actual).isNotNull())
                .verifyComplete();
        verify(studyRepository).findByUserIdAndStudyDeck_DeckIdAndCompletedFalse(anyString(), anyString());
    }

    @Test
    void whenUserHasNonPendingStudyForDeckThenThrowError() {
        when(studyRepository.findByUserIdAndStudyDeck_DeckIdAndCompletedFalse(anyString(), anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(studyQueryService
                        .findPendingStudyByUserIdAndDeckId(ObjectId.get().toString(), ObjectId.get().toString()))
                .verifyError(NotFoundException.class);
        verify(studyRepository).findByUserIdAndStudyDeck_DeckIdAndCompletedFalse(anyString(), anyString());
    }

    @Test
    void findAllByUserIdTest() {
        var deck1 = DeckDocumentFactoryBot.builder().build();
        var study1 = StudyDocumentFactoryBot.builder(ObjectId.get().toString(), deck1).build();
        var deck2 = DeckDocumentFactoryBot.builder().build();
        var study2 = StudyDocumentFactoryBot.builder(ObjectId.get().toString(), deck2).build();
        when(studyRepository.findAllByUserId(anyString())).thenReturn(Flux.fromIterable(List.of(study1, study2)));

        StepVerifier.create(studyQueryService.findAllByUserId(ObjectId.get().toString()))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> assertThat(actual.size()).isEqualTo(2))
                .verifyComplete();
        verify(studyRepository).findAllByUserId(anyString());
    }

    @Test
    void whenStudyNotFinishedThenReturnIt() {
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(ObjectId.get().toString(), deck).build();

        StepVerifier.create(studyQueryService.verifyIfFinished(study))
                .assertNext(actual -> assertThat(actual).isNotNull())
                .verifyComplete();
        verifyNoInteractions(studyRepository);
    }

    @Test
    void whenStudyFinishedThenThrowError() {
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(ObjectId.get().toString(), deck).finishedStudy().build();

        StepVerifier.create(studyQueryService.verifyIfFinished(study))
                .verifyError(NotFoundException.class);
        verifyNoInteractions(studyRepository);
    }

    @Test
    void getLastPendingQuestionTest() {
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(ObjectId.get().toString(), deck).build();
        when(studyRepository.findById(anyString())).thenReturn(Mono.just(study));

        StepVerifier.create(studyQueryService.getLastPendingQuestion(ObjectId.get().toString()))
                .assertNext(actual -> {
                    assertThat(actual.answered()).isNull();
                    assertThat(actual.answeredIn()).isNull();
                })
                .verifyComplete();
        verify(studyRepository).findById(anyString());
    }

    @Test
    void whenTryToGetPendingQuestionFromNonStoredStudyThenThrowError() {
        when(studyRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(studyQueryService.getLastPendingQuestion(ObjectId.get().toString()))
                .verifyError(NotFoundException.class);
        verify(studyRepository).findById(anyString());
    }

}
