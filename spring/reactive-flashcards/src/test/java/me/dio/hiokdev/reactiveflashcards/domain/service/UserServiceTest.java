package me.dio.hiokdev.reactiveflashcards.domain.service;

import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.UserDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.EmailAlreadyUsedException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepository;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserQueryService userQueryService;
    private UserService userService;

    @BeforeEach
    void setup() {
        this.userService = new UserService(userRepository, userQueryService);
    }

    @Test
    void saveTest() {
        var document = UserDocumentFactoryBot.builder().build();
        when(userQueryService.findByEmail(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        when(userRepository.save(any(UserDocument.class))).thenAnswer(invocationOnMock -> {
            var user = invocationOnMock.getArgument(0, UserDocument.class);
            return Mono.just(user.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(userService.save(document))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.id()).isNotNull();
                    assertThat(actual.createdAt()).isNotNull();
                    assertThat(actual.updatedAt()).isNotNull();
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("id", "createdAt", "updatedAt")
                            .isEqualTo(document);
                })
                .verifyComplete();
        verify(userQueryService).findByEmail(anyString());
        verify(userRepository).save(any(UserDocument.class));
    }

    @Test
    void whenTryToSaveUserWithExistingEmailThenThrowError() {
        var document = UserDocumentFactoryBot.builder().build();
        when(userQueryService.findByEmail(anyString())).thenReturn(Mono.just(document));

        StepVerifier.create(userService.save(document))
                .verifyError(EmailAlreadyUsedException.class);
        verify(userQueryService).findByEmail(anyString());
        verify(userRepository, times(0)).save(any(UserDocument.class));
    }

    private static Stream<Arguments> updateTest(){
        var existDocument = UserDocumentFactoryBot.builder().build();
        var documentToUpdate = existDocument.toBuilder().name("New name").build();
        return Stream.of(
                Arguments.of(documentToUpdate, Mono.just(existDocument), existDocument),
                Arguments.of(documentToUpdate, Mono.error(new NotFoundException("")), existDocument)
        );
    }

    @MethodSource
    @ParameterizedTest
    void updateTest(final UserDocument documentToUpdate, final Mono<UserDocument> mockFindByEmail, final UserDocument mockFindById) {
        when(userQueryService.findByEmail(anyString())).thenReturn(mockFindByEmail);
        when(userQueryService.findById(anyString())).thenReturn(Mono.just(mockFindById));
        when(userRepository.save(any(UserDocument.class))).thenAnswer(invocationOnMock -> {
          var user = invocationOnMock.getArgument(0, UserDocument.class);
          return Mono.just(user.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });

        StepVerifier.create(userService.update(documentToUpdate))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isNotEqualTo(mockFindById);
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(documentToUpdate);
                })
                .verifyComplete();
        verify(userQueryService).findByEmail(anyString());
        verify(userQueryService).findById(anyString());
        verify(userRepository).save(any(UserDocument.class));
    }

    @Test
    void whenTryToUpdateUserNonStoredThenThrowError() {
        var document = UserDocumentFactoryBot.builder().build();
        when(userQueryService.findByEmail(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        when(userQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(userService.update(document))
                .verifyError(NotFoundException.class);
        verify(userQueryService).findByEmail(anyString());
        verify(userQueryService).findById(anyString());
        verify(userRepository, times(0)).save(any(UserDocument.class));
    }

    @Test
    void whenTryToUpdateUserWithEmailUsedByOtherThenThrowError() {
        var existDocument = UserDocumentFactoryBot.builder().build();
        var documentToSave = UserDocumentFactoryBot.builder().build();
        when(userQueryService.findByEmail(anyString())).thenReturn(Mono.just(existDocument));

        StepVerifier.create(userService.update(documentToSave))
                .verifyError(EmailAlreadyUsedException.class);
        verify(userQueryService).findByEmail(anyString());
        verify(userQueryService, times(0)).findById(anyString());
        verify(userRepository, times(0)).save(any(UserDocument.class));
    }

    @Test
    void deleteTest() {
        var document = UserDocumentFactoryBot.builder().build();
        when(userQueryService.findById(anyString())).thenReturn(Mono.just(document));
        when(userRepository.delete(any(UserDocument.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.delete(document.id()))
                .verifyComplete();
        verify(userQueryService).findById(anyString());
        verify(userRepository).delete(any(UserDocument.class));
    }

    @Test
    void whenTryToDeleteNonStoredUserThenThrowError() {
        when(userQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(userService.delete(ObjectId.get().toString()))
                .verifyError(NotFoundException.class);
        verify(userQueryService).findById(anyString());
        verifyNoInteractions(userRepository);
    }

}
