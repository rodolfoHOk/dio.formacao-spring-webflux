package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.repository.DeckRepository;
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
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DeckQueryServiceTest {

    @Mock
    private DeckRepository deckRepository;

    private DeckQueryService deckQueryService;

    @BeforeEach
    void setup() {
        deckQueryService = new DeckQueryService(deckRepository);
    }

    @Test
    void findAllTest() {
        var faker = RandomData.getFaker();
        var documents = Stream.generate(() -> DeckDocumentFactoryBot.builder().build())
                .limit(faker.number().randomDigitNotZero())
                .toList();
        when(deckRepository.findAll()).thenReturn(Flux.fromIterable(documents));

        StepVerifier.create(deckQueryService.findAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> assertThat(actual.size()).isEqualTo(documents.size()))
                .verifyComplete();
        verify(deckRepository).findAll();
    }

    @Test
    void findByIdTest() {
        var document = DeckDocumentFactoryBot.builder().build();
        when(deckRepository.findById(anyString())).thenReturn(Mono.just(document));

        StepVerifier.create(deckQueryService.findById(document.id()))
                .assertNext(actual -> assertThat(actual).isEqualTo(document))
                .verifyComplete();
        verify(deckRepository).findById(anyString());
    }

    @Test
    void whenTryToFindNonStoredDeckThenThrowError() {
        var id = ObjectId.get().toString();
        when(deckRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(deckQueryService.findById(id))
                .verifyErrorMessage(BaseErrorMessage.DECK_NOT_FOUND.params(id).getMessage());
        verify(deckRepository).findById(anyString());
    }

}
