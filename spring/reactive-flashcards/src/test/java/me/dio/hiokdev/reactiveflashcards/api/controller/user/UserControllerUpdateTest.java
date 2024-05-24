package me.dio.hiokdev.reactiveflashcards.api.controller.user;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.UserController;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.UserMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.request.UserRequestFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.EmailAlreadyUsedException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.service.UserService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
public class UserControllerUpdateTest extends AbstractControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private UserQueryService userQueryService;
    private RequestBuilder<UserResponse> userResponseRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;

    @BeforeEach
    void setup() {
        userResponseRequestBuilder = RequestBuilder
                .userResponseRequestBuilder(applicationContext, "/users");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/users");
    }

    @Test
    void updateTest() {
        when(userService.update(any(UserDocument.class))).thenAnswer(invocationOnMock -> {
            var documento = invocationOnMock.getArgument(0, UserDocument.class);
            return Mono.just(documento.toBuilder()
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        var userId = ObjectId.get().toString();
        var requestBody = UserRequestFactoryBot.builder().build();
        userResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(userId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPut()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(requestBody);
                });
    }

    @Test
    void whenTryUpdateNonStoredUserThenReturnNotFound() {
        when(userService.update(any(UserDocument.class))).thenReturn(Mono.error(new NotFoundException("")));

        var userId = ObjectId.get().toString();
        var requestBody = UserRequestFactoryBot.builder().build();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(userId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPut()
                .httpStatusIsNotFound()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    void whenTryUseEmailInUseThenReturnBadRequest() {
        when(userService.update(any(UserDocument.class))).thenReturn(Mono.error(new EmailAlreadyUsedException("")));

        var userId = ObjectId.get().toString();
        var requestBody = UserRequestFactoryBot.builder().build();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(userId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPut()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

    private static Stream<Arguments> checkConstraintsTest() {
        var validId = ObjectId.get().toString();
        var invalidId = faker.lorem().word();
        return Stream.of(
                Arguments.of(invalidId, UserRequestFactoryBot.builder().build(), "id"),
                Arguments.of(validId, UserRequestFactoryBot.builder().blankName().build(), "name"),
                Arguments.of(validId, UserRequestFactoryBot.builder().longName().build(), "name"),
                Arguments.of(validId, UserRequestFactoryBot.builder().blankEmail().build(), "email"),
                Arguments.of(validId, UserRequestFactoryBot.builder().longEmail().build(), "email"),
                Arguments.of(validId, UserRequestFactoryBot.builder().invalidEmail().build(), "email")
        );
    }

    @MethodSource
    @ParameterizedTest
    void checkConstraintsTest(final String userId, final UserRequest requestBody, final String field) {
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(userId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPut()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(response.fields().stream().map(ErrorFieldResponse::name).toList()).contains(field);
                });
    }

}
