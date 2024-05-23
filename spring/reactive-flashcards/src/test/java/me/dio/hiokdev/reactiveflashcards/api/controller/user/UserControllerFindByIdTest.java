package me.dio.hiokdev.reactiveflashcards.api.controller.user;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.UserController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.UserMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.UserDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.service.UserService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
public class UserControllerFindByIdTest extends AbstractControllerTest {

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
    void findByIdTest() {
        var user = UserDocumentFactoryBot.builder().build();
        when(userQueryService.findById(anyString())).thenReturn(Mono.just(user));
        userResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(user.id()))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(user);
                });
    }

    @Test
    void whenTryToFindNonStoredUserThenReturnNotFound() {
        when(userQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var userId = ObjectId.get().toString();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(userId))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsNotFound()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    void whenTryUseNonValidIdThenReturnBadRequest() {
        var invalidId = faker.lorem().word();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(invalidId))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

}
