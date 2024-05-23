package me.dio.hiokdev.reactiveflashcards.api.controller.deck;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.ReactiveFlashcardsApplication;
import me.dio.hiokdev.reactiveflashcards.api.controller.DeckController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.ApiExceptionHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ConstraintViolationHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.DeckInStudyHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.EmailAlreadyUsedHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.GenericExceptionHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.JsonProcessingHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.MethodNotAllowedHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.NotFoundHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ReactiveFlashCardsHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ResponseStatusHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.WebExchangeBindHandler;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapper;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.mongo.provider.OffsetDateTimeProvider;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.service.DeckService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(DeckController.class)
@ContextConfiguration(classes = {DeckMapperImpl.class, OffsetDateTimeProvider.class, ApiExceptionHandler.class,
        DeckInStudyHandler.class, EmailAlreadyUsedHandler.class, MethodNotAllowedHandler.class, NotFoundHandler.class,
        ConstraintViolationHandler.class, WebExchangeBindHandler.class, ResponseStatusHandler.class,
        ReactiveFlashCardsHandler.class, GenericExceptionHandler.class, JsonProcessingHandler.class,
        ReactiveFlashcardsApplication.class, MongoMappingContext.class})
public class DeckControllerDeleteTest {

    @MockBean
    public DeckService deckService;
    @MockBean
    public DeckQueryService deckQueryService;
    @MockBean
    public DeckMapper deckMapper;

    @Autowired
    private ApplicationContext applicationContext;
    private RequestBuilder<Void> noContentRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;
    private final Faker faker = RandomData.getFaker();

    @BeforeEach
    void setup() {
        noContentRequestBuilder = RequestBuilder
                .noContentRequestBuilder(applicationContext, "/decks");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/decks");
    }

    @Test
    void deleteTest() {
        var deckId = ObjectId.get().toString();
        when(deckService.delete(anyString())).thenReturn(Mono.empty());
        noContentRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(deckId))
                .generateRequestWithoutBody()
                .doDelete()
                .httpStatusIsNoContent();
    }

    @Test
    void whenTryToDeleteNonStoredDeckThenReturnNotFound(){
        var deckId = ObjectId.get().toString();
        when(deckService.delete(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(deckId))
                .generateRequestWithSimpleBody()
                .doDelete()
                .httpStatusIsNotFound()
                .assertBody(actual ->{
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    void whenTryUseInvalidIdThenReturnBadRequest(){
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(faker.lorem().word()))
                .generateRequestWithSimpleBody()
                .doDelete()
                .httpStatusIsBadRequest()
                .assertBody(actual ->{
                    assertThat(actual).isNotNull();
                    assertThat(actual.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(actual.fields().stream().map(ErrorFieldResponse::name).toList()).contains("id");
                });
    }

}
