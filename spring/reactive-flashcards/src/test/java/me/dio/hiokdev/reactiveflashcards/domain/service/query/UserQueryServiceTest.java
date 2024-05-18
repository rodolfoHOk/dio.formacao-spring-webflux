package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.UserDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.request.UserPageRequestFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepository;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepositoryImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRepositoryImpl userRepositoryImpl;
    private UserQueryService userQueryService;
    private final static Faker faker = RandomData.getFaker();

    @BeforeEach
    void setup() {
        this.userQueryService = new UserQueryService(userRepository, userRepositoryImpl);
    }

    @Test
    void findByIdTest() {
        var document = UserDocumentFactoryBot.builder().build();
        when(userRepository.findById(anyString())).thenReturn(Mono.just(document));

        StepVerifier.create(userQueryService.findById(document.id()))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(document);
                })
                .verifyComplete();
        verify(userRepository).findById(anyString());
        verifyNoInteractions(userRepositoryImpl);
    }

    @Test
    void whenTryToFindNonStoredUserByIdThenThrowError() {
        when(userRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userQueryService.findById(ObjectId.get().toString()))
                .verifyError(NotFoundException.class);
        verify(userRepository).findById(anyString());
        verifyNoInteractions(userRepositoryImpl);
    }

    @Test
    void findByEmailTest() {
        var document = UserDocumentFactoryBot.builder().build();
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(document));

        StepVerifier.create(userQueryService.findByEmail(faker.internet().emailAddress()))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(document);
                })
                .verifyComplete();
        verify(userRepository).findByEmail(anyString());
        verifyNoInteractions(userRepositoryImpl);
    }

    @Test
    void whenTryToFindNonStoredUserByEmailThenThrowError() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userQueryService.findByEmail(faker.internet().emailAddress()))
                .verifyError(NotFoundException.class);
        verify(userRepository).findByEmail(anyString());
        verifyNoInteractions(userRepositoryImpl);
    }

    private static Stream<Arguments> findOnDemandTest() {
        var documents = Stream.generate(() -> UserDocumentFactoryBot.builder().build())
                .limit(faker.number().randomDigitNotZero())
                .toList();
        var total = faker.number().numberBetween(documents.size(), documents.size() * 3L);
        var pageRequest = UserPageRequestFactoryBot.builder().build();
        var totalPages = total / pageRequest.limit() + ((total % pageRequest.limit() > 0) ? 1 : 0);
        return Stream.of(
                Arguments.of(documents, total, pageRequest, totalPages),
                Arguments.of(List.of(), 0L, pageRequest, 0L)
        );
    }

    @MethodSource
    @ParameterizedTest
    void findOnDemandTest(
            final List<UserDocument> documents,
            final Long total,
            final UserPageRequest pageRequest,
            final Long totalPages
    ) {
        when(userRepositoryImpl.findOnDemand(any(UserPageRequest.class))).thenReturn(Flux.fromIterable(documents));
        when(userRepositoryImpl.count(any(UserPageRequest.class))).thenReturn(Mono.just(total));

        StepVerifier.create(userQueryService.findOnDemand(pageRequest))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.content()).containsExactlyInAnyOrderElementsOf(documents);
                    assertThat(actual.totalItens()).isEqualTo(total);
                    assertThat(actual.totalPages()).isEqualTo(totalPages);
                })
                .verifyComplete();
        verify(userRepositoryImpl).findOnDemand(any(UserPageRequest.class));
        verify(userRepositoryImpl).count(any(UserPageRequest.class));
        verifyNoInteractions(userRepository);
    }

}
