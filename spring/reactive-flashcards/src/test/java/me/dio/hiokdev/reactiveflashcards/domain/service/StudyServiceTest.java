package me.dio.hiokdev.reactiveflashcards.domain.service;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.UserDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyCard;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDeck;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.MailMessageDTO;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
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
    private static final Faker faker = RandomData.getFaker();

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

    private static Stream<Arguments> answerTest() {
        var userId = ObjectId.get().toString();
        var deck = DeckDocumentFactoryBot.builder().build();
        var studyWithOneRemainQuestion = StudyDocumentFactoryBot.builder(userId, deck).pendingQuestions(1).build();
        var studyWithTwoRemainQuestions = StudyDocumentFactoryBot.builder(userId, deck).pendingQuestions(2).build();
        var answerForOneRemainQuestion = faker.lorem().word();
        var answerForTwoRemainQuestions = faker.lorem().word();
        Consumer<StudyDocument> nextSameQuestion = actual -> {
            var pendingQuestion = actual.getLastPendingQuestion();
            assertThat(pendingQuestion).isNotNull();
            assertThat(pendingQuestion.asked()).isEqualTo(actual.getLastAnsweredQuestion().asked());
        };
        Consumer<StudyDocument> nextDiffQuestion = actual -> {
            var pendingQuestion = actual.getLastPendingQuestion();
            assertThat(pendingQuestion).isNotNull();
            assertThat(pendingQuestion.asked()).isNotEqualTo(actual.getLastAnsweredQuestion().asked());
        };
        return Stream.of(
                Arguments.of(studyWithOneRemainQuestion, answerForOneRemainQuestion, nextSameQuestion),
                Arguments.of(studyWithTwoRemainQuestions, answerForTwoRemainQuestions, nextDiffQuestion)
        );
    }

    @MethodSource
    @ParameterizedTest
    void answerTest(final StudyDocument study, final String answer, Consumer<StudyDocument> asserts) {
        when(studyQueryService.findById(anyString())).thenReturn(Mono.just(study));
        when(studyQueryService.verifyIfFinished(any(StudyDocument.class)))
                .thenReturn(Mono.just(study));
        when(studyRepository.save(any(StudyDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, StudyDocument.class);
            return Mono.just(document.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });

        StepVerifier.create(studyService.answer(study.id(), answer))
                .assertNext(asserts)
                .verifyComplete();
        verify(studyQueryService).findById(anyString());
        verify(studyQueryService).verifyIfFinished(any(StudyDocument.class));
        verify(studyRepository).save(any(StudyDocument.class));
        verifyNoInteractions(userQueryService);
        verifyNoInteractions(deckQueryService);
        verifyNoInteractions(mailService);
    }

    @Test
    void whenStudyIsFinishedThenSendEmail() throws InterruptedException {
        var user = UserDocumentFactoryBot.builder().build();
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(user.id(), deck).pendingQuestions(1).build();
        var mailCaptor = ArgumentCaptor.forClass(MailMessageDTO.class);
        when(studyQueryService.findById(anyString())).thenReturn(Mono.just(study));
        when(studyQueryService.verifyIfFinished(any(StudyDocument.class)))
                .thenReturn(Mono.just(study));
        when(studyRepository.save(any(StudyDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, StudyDocument.class);
            return Mono.just(document.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });
        when(userQueryService.findById(anyString())).thenReturn(Mono.just(user));
        when(deckQueryService.findById(anyString())).thenReturn(Mono.just(deck));
        when(mailService.send(mailCaptor.capture())).thenReturn(Mono.empty());

        var answer = study.getLastPendingQuestion().expected();
        StepVerifier.create(studyService.answer(study.id(), answer))
                .assertNext(actual -> assertThat(actual.completed()).isTrue())
                .verifyComplete();
        TimeUnit.SECONDS.sleep(2);
        verify(studyQueryService).findById(anyString());
        verify(studyQueryService).verifyIfFinished(any(StudyDocument.class));
        verify(studyRepository).save(any(StudyDocument.class));
        verify(userQueryService).findById(anyString());
        verify(deckQueryService).findById(anyString());
        verify(mailService).send(any(MailMessageDTO.class));
    }

    @Test
    void whenHasNonStudyStoredThenThrowError() {
        when(studyQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(studyService.answer(ObjectId.get().toString(), faker.lorem().word()))
                .verifyError(NotFoundException.class);
        verify(studyQueryService).findById(anyString());
        verify(studyQueryService, times(0)).verifyIfFinished(any(StudyDocument.class));
        verifyNoInteractions(studyRepository);
        verifyNoInteractions(userQueryService);
        verifyNoInteractions(deckQueryService);
        verifyNoInteractions(mailService);
    }

    @Test
    void whenStudyHasNonPendingQuestionsThenThrowError() {
        var userId = ObjectId.get().toString();
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(userId, deck).build();
        when(studyQueryService.findById(anyString())).thenReturn(Mono.just(study));
        when(studyQueryService.verifyIfFinished(any(StudyDocument.class)))
                .thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(studyService.answer(ObjectId.get().toString(), faker.lorem().word()))
                .verifyError(NotFoundException.class);
        verify(studyQueryService).findById(anyString());
        verify(studyQueryService).verifyIfFinished(any(StudyDocument.class));
        verifyNoInteractions(studyRepository);
        verifyNoInteractions(userQueryService);
        verifyNoInteractions(deckQueryService);
        verifyNoInteractions(mailService);
    }

}
