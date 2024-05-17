package me.dio.hiokdev.reactiveflashcards.domain.service;

import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.dto.DeckRestDTOFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.DeckDomainMapper;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.DeckDomainMapperImpl;
import me.dio.hiokdev.reactiveflashcards.domain.repository.DeckRepository;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckRestQueryService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DeckServiceTest {

    @Mock
    private DeckRepository deckRepository;
    @Mock
    private DeckQueryService deckQueryService;
    @Mock
    private DeckRestQueryService deckRestQueryService;
    private final DeckDomainMapper deckDomainMapper = new DeckDomainMapperImpl();
    private DeckService deckService;

    @BeforeEach
    void setup() {
        deckService = new DeckService(deckRepository, deckQueryService, deckRestQueryService, deckDomainMapper);
    }

    @Test
    void saveTest() {
        var document = DeckDocumentFactoryBot.builder().preInsert().build();
        when(deckRepository.save(any())).thenAnswer(invocation -> {
            var deck = invocation.getArgument(0, DeckDocument.class);
            return Mono.just(deck.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(deckService.save(document))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).hasNoNullFieldsOrProperties();
                })
                .verifyComplete();
        verify(deckRepository).save(any());
        verifyNoInteractions(deckQueryService);
        verifyNoInteractions(deckRestQueryService);
    }

    @Test
    void updateTest() {
        var storedDeck = DeckDocumentFactoryBot.builder().build();
        var document = DeckDocumentFactoryBot.builder().preUpdate(storedDeck.id()).build();
        when(deckQueryService.findById(anyString())).thenReturn(Mono.just(storedDeck));
        when(deckRepository.save(any())).thenAnswer(invocationOnMock -> {
            var deck = invocationOnMock.getArgument(0, DeckDocument.class);
            return Mono.just(deck.toBuilder()
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(deckService.update(document))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(document);
                    assertThat(actual.createdAt().toEpochSecond()).isEqualTo(storedDeck.createdAt().toEpochSecond());
                    assertThat(actual.updatedAt()).isNotEqualTo(storedDeck.updatedAt());
                })
                .verifyComplete();
        verify(deckRepository).save(any());
        verify(deckQueryService).findById(anyString());
        verifyNoInteractions(deckRestQueryService);
    }

    @Test
    void whenTryToUpdateNonStoredDeckThenThrowError() {
        var document = DeckDocumentFactoryBot.builder().build();
        when(deckQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(deckService.update(document))
                .verifyError(NotFoundException.class);
        verify(deckRepository, times(0)).save(any());
        verify(deckQueryService).findById(anyString());
        verifyNoInteractions(deckRestQueryService);
    }

    @Test
    void deleteTest() {
        var deckCaptor = ArgumentCaptor.forClass(DeckDocument.class);
        var storedDeck = DeckDocumentFactoryBot.builder().build();
        when(deckQueryService.findById(anyString())).thenReturn(Mono.just(storedDeck));
        when(deckRepository.delete(deckCaptor.capture())).thenReturn(Mono.empty());

        StepVerifier.create(deckService.delete(storedDeck.id())).verifyComplete();
        var capturedDeck = deckCaptor.getValue();
        assertThat(capturedDeck).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(storedDeck);
        assertThat(capturedDeck.createdAt().toEpochSecond()).isEqualTo(storedDeck.createdAt().toEpochSecond());
        assertThat(capturedDeck.createdAt().toEpochSecond()).isEqualTo(storedDeck.createdAt().toEpochSecond());
        verify(deckRepository).delete(any(DeckDocument.class));
        verify(deckQueryService).findById(anyString());
        verifyNoInteractions(deckRestQueryService);
    }

    @Test
    void whenTryToDeleteNonStoredDeckThenThrowError() {
        var id = ObjectId.get().toString();
        when(deckQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(deckService.delete(id))
                .verifyError(NotFoundException.class);
        verify(deckRepository, times(0)).delete(any(DeckDocument.class));
        verify(deckQueryService).findById(anyString());
        verifyNoInteractions(deckRestQueryService);
    }

    @Test
    void syncTest() throws InterruptedException {
        var deckCaptor = ArgumentCaptor.forClass(DeckDocument.class);
        var faker = RandomData.getFaker();
        var externalDecks = Stream.generate(() -> DeckRestDTOFactoryBot.builder().build())
                .limit(faker.number().randomDigitNotZero())
                .toList();
        when(deckRestQueryService.getDecks()).thenReturn(Flux.fromIterable(externalDecks));
        when(deckRepository.save(deckCaptor.capture())).thenAnswer(invocation -> {
            var deck = invocation.getArgument(0, DeckDocument.class);
            return Mono.just(deck.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(deckService.sync()).verifyComplete();
        TimeUnit.SECONDS.sleep(2);

        assertThat(deckCaptor.getAllValues().size()).isEqualTo(externalDecks.size());
        verify(deckRestQueryService).getDecks();
        verify(deckRepository, times(externalDecks.size())).save(any());
        verifyNoInteractions(deckQueryService);
    }

}
