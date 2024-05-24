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
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
public class UserControllerSaveTest extends AbstractControllerTest {

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
    void saveTest() {
        when(userService.save(any(UserDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, UserDocument.class);
            return Mono.just(document.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        var requestBody = UserRequestFactoryBot.builder().build();
        userResponseRequestBuilder.uri(UriBuilder::build)
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsCreated()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.id()).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(requestBody);
                });
    }

    @Test
    void whenTryUseEmailInUseThenReturnBadRequest() {
        when(userService.save(any(UserDocument.class))).thenReturn(Mono.error(new EmailAlreadyUsedException("")));

        var requestBody = UserRequestFactoryBot.builder().build();
        problemResponseRequestBuilder.uri(UriBuilder::build)
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

    private static Stream<Arguments> checkConstraintsTest() {
        return Stream.of(
                Arguments.of(UserRequestFactoryBot.builder().blankName().build(), "name"),
                Arguments.of(UserRequestFactoryBot.builder().longName().build(), "name"),
                Arguments.of(UserRequestFactoryBot.builder().blankEmail().build(), "email"),
                Arguments.of(UserRequestFactoryBot.builder().longEmail().build(), "email"),
                Arguments.of(UserRequestFactoryBot.builder().invalidEmail().build(), "email")
        );
    }

    @MethodSource
    @ParameterizedTest
    void checkConstraintsTest(final UserRequest requestBody, final String field) {
        problemResponseRequestBuilder.uri(UriBuilder::build)
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(response.fields().stream().map(ErrorFieldResponse::name).toList()).contains(field);
                });
    }

}
