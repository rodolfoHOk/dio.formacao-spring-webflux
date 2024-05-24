package me.dio.hiokdev.reactiveflashcards.api.controller.user;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.UserController;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserPageResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.UserMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.dto.UserPageDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.request.UserPageRequestFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.dto.UserPageDocument;
import me.dio.hiokdev.reactiveflashcards.domain.service.UserService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
public class UserControllerFindOnDemandTest extends AbstractControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private UserQueryService userQueryService;
    private RequestBuilder<UserPageResponse> userPageResponseRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;

    @BeforeEach
    void setup() {
        userPageResponseRequestBuilder = RequestBuilder
                .userPageResponseRequestBuilder(applicationContext, "/users");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/users");
    }

    private static Stream<UserPageDocument> findOnDemandTest() {
        var pageDocumentNonEmpty = UserPageDocumentFactoryBot.builder().build();
        var pageDocumentEmpty = UserPageDocumentFactoryBot.builder().emptyPage().build();
        return Stream.of(
                pageDocumentNonEmpty,
                pageDocumentEmpty
        );
    }

    @MethodSource
    @ParameterizedTest
    void findOnDemandTest(final UserPageDocument pageDocument) {
        when(userQueryService.findOnDemand(any(UserPageRequest.class))).thenReturn(Mono.just(pageDocument));

        var queryParams = UserPageRequestFactoryBot.builder().build();
        userPageResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .queryParam("sentence", queryParams.sentence())
                        .queryParam("page", queryParams.page())
                        .queryParam("limit", queryParams.limit())
                        .queryParam("sortBy", queryParams.sortBy())
                        .queryParam("sortDirection", queryParams.sortDirection())
                        .build())
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.content().size()).isEqualTo(pageDocument.content().size());
                });
    }

    private static Stream<Arguments> checkConstraintsTest() {
        var lessThanZeroLimitParams = UserPageRequestFactoryBot.builder().lessThanZeroLimit().build();
        Function<UriBuilder, URI> limitMinBadRequestUri = uriBuilder -> uriBuilder
                .queryParam("sentence", lessThanZeroLimitParams.sentence())
                .queryParam("page", lessThanZeroLimitParams.page())
                .queryParam("limit", lessThanZeroLimitParams.limit())
                .queryParam("sortBy", lessThanZeroLimitParams.sortBy())
                .queryParam("sortDirection", lessThanZeroLimitParams.sortDirection())
                .build();
        var greaterThanFiftyLimitParams = UserPageRequestFactoryBot.builder().greaterThanFiftyLimit().build();
        Function<UriBuilder, URI> limitMaxBadRequestUri = uriBuilder -> uriBuilder
                .queryParam("sentence", greaterThanFiftyLimitParams.sentence())
                .queryParam("page", greaterThanFiftyLimitParams.page())
                .queryParam("limit", greaterThanFiftyLimitParams.limit())
                .queryParam("sortBy", greaterThanFiftyLimitParams.sortBy())
                .queryParam("sortDirection", greaterThanFiftyLimitParams.sortDirection())
                .build();
        var negativePageParams = UserPageRequestFactoryBot.builder().negativePage().build();
        Function<UriBuilder, URI> pagePositiveOrZeroBadRequestUri = uriBuilder -> uriBuilder
                .queryParam("sentence", negativePageParams.sentence())
                .queryParam("page", negativePageParams.page())
                .queryParam("limit", negativePageParams.limit())
                .queryParam("sortBy", negativePageParams.sortBy())
                .queryParam("sortDirection", negativePageParams.sortDirection())
                .build();
        return Stream.of(
                Arguments.of(limitMinBadRequestUri, "limit"),
                Arguments.of(limitMaxBadRequestUri, "limit"),
                Arguments.of(pagePositiveOrZeroBadRequestUri, "page")
        );
    }

    @MethodSource
    @ParameterizedTest
    void checkConstraintsTest(final Function<UriBuilder, URI> uriFunction, final String field) {
        problemResponseRequestBuilder.uri(uriFunction)
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(response.fields().stream().map(ErrorFieldResponse::name).toList()).contains(field);
                });
    }

}
