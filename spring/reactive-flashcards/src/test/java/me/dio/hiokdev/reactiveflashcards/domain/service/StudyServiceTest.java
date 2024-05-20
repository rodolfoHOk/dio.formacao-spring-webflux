package me.dio.hiokdev.reactiveflashcards.domain.service;

import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.UserDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyCard;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDeck;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.DeckInStudyException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapper;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapperImpl;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapperImpl_;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.StudyDomainMapper;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.StudyDomainMapperImpl;
import me.dio.hiokdev.reactiveflashcards.domain.repository.StudyRepository;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class StudyServiceTest {

    @Mock
    private UserQueryService userQueryService;
    @Mock
    private DeckQueryService deckQueryService;
    @Mock
    private StudyQueryService studyQueryService;
    private final StudyDomainMapper studyDomainMapper = new StudyDomainMapperImpl();
    @Mock
    private StudyRepository studyRepository;
    @Mock
    private MailService mailService;
    private final MailMapper mailMapper = new MailMapperImpl(new MailMapperImpl_());
    private StudyService studyService;

    @BeforeEach
    void setup() {
        this.studyService = new StudyService(
                userQueryService, deckQueryService, studyQueryService, studyDomainMapper,
                studyRepository, mailService, mailMapper);
    }

    @Test
    void startTest() {
        var user = UserDocumentFactoryBot.builder().build();
        var deck = DeckDocumentFactoryBot.builder().build();
        when(studyQueryService.findPendingStudyByUserIdAndDeckId(anyString(), anyString()))
                .thenReturn(Mono.error(new NotFoundException("")));
        when(userQueryService.findById(anyString())).thenReturn(Mono.just(user));
        when(deckQueryService.findById(anyString())).thenReturn(Mono.just(deck));
        when(studyRepository.save(any(StudyDocument.class))).thenAnswer(invocationOnMock -> {
            var study = invocationOnMock.getArgument(0, StudyDocument.class);
            return Mono.just(study.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        var studyDeck = StudyDeck.builder().deckId(deck.id()).build();
        var study = StudyDocument.builder().userId(user.id()).studyDeck(studyDeck).build();
        StepVerifier.create(studyService.start(study))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.completed()).isFalse();
                    assertThat(actual.userId()).isEqualTo(user.id());
                    assertThat(actual.studyDeck().cards()).containsExactlyInAnyOrderElementsOf(deck.cards().stream()
                            .map(card -> StudyCard.builder().front(card.front()).back(card.back()).build())
                            .collect(Collectors.toSet()));
                    assertThat(actual.questions().size()).isOne();
                    var question = actual.questions().getFirst();
                    assertThat(actual.studyDeck().cards()).contains(StudyCard.builder()
                            .front(question.asked())
                            .back(question.expected())
                            .build());
                    assertThat(question.isNotAnswered()).isTrue();
                })
                .verifyComplete();
        verify(studyQueryService).findPendingStudyByUserIdAndDeckId(anyString(), anyString());
        verify(userQueryService).findById(anyString());
        verify(deckQueryService).findById(anyString());
        verify(studyRepository).save(any(StudyDocument.class));
    }

    @Test
    void whenUserTryToStartStudyWithDeckTwoTimesThenThrowError() {
        var userId = ObjectId.get().toString();
        var deck = DeckDocumentFactoryBot.builder().build();
        var existStudy = StudyDocumentFactoryBot.builder(userId, deck).build();
        when(studyQueryService.findPendingStudyByUserIdAndDeckId(anyString(), anyString()))
                .thenReturn(Mono.just(existStudy));

        var studyDeck = StudyDeck.builder().deckId(deck.id()).build();
        var study = StudyDocument.builder().userId(userId).studyDeck(studyDeck).build();
        StepVerifier.create(studyService.start(study))
                .verifyError(DeckInStudyException.class);
        verify(studyQueryService).findPendingStudyByUserIdAndDeckId(anyString(), anyString());
        verifyNoInteractions(userQueryService);
        verifyNoInteractions(deckQueryService);
        verifyNoInteractions(studyRepository);
    }

    @Test
    void whenNonStoredUserTryToStartStudyThenThrowError() {
        when(studyQueryService.findPendingStudyByUserIdAndDeckId(anyString(), anyString()))
                .thenReturn(Mono.error(new NotFoundException("")));
        when(userQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        var userId = ObjectId.get().toString();
        var deckId = ObjectId.get().toString();
        var studyDeck = StudyDeck.builder().deckId(deckId).build();
        var study = StudyDocument.builder().userId(userId).studyDeck(studyDeck).build();
        StepVerifier.create(studyService.start(study))
                .verifyError(NotFoundException.class);
        verify(studyQueryService).findPendingStudyByUserIdAndDeckId(anyString(), anyString());
        verify(userQueryService).findById(anyString());
        verifyNoInteractions(deckQueryService);
        verifyNoInteractions(studyRepository);
    }

    @Test
    void whenUserTryToStudyNonStoredDeckThenThrowError() {
        var user = UserDocumentFactoryBot.builder().build();
        when(studyQueryService.findPendingStudyByUserIdAndDeckId(anyString(), anyString()))
                .thenReturn(Mono.error(new NotFoundException("")));
        when(userQueryService.findById(anyString())).thenReturn(Mono.just(user));
        when(deckQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        var deckId = ObjectId.get().toString();
        var studyDeck = StudyDeck.builder().deckId(deckId).build();
        var study = StudyDocument.builder().userId(user.id()).studyDeck(studyDeck).build();
        StepVerifier.create(studyService.start(study))
                .verifyError(NotFoundException.class);
        verify(studyQueryService).findPendingStudyByUserIdAndDeckId(anyString(), anyString());
        verify(userQueryService).findById(anyString());
        verify(deckQueryService).findById(anyString());
        verifyNoInteractions(studyRepository);
    }

}
