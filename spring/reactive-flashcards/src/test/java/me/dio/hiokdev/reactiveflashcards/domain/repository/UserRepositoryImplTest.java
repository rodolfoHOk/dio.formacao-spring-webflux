package me.dio.hiokdev.reactiveflashcards.domain.repository;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserSortBy;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserSortDirection;
import me.dio.hiokdev.reactiveflashcards.core.MongoDBTestConfig;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.UserDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.request.UserPageRequestFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MongoDBTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRepositoryImpl userRepositoryImpl;
    private final Faker faker = RandomData.getFaker();
    private final List<UserDocument> storedDocuments = new ArrayList<>();

    @BeforeEach
    void setup() {
        var users = Stream.generate(() -> UserDocumentFactoryBot.builder().build())
                .limit(15)
                .toList();
        var savedUsers = userRepository.saveAll(users).collectList().block();
        storedDocuments.addAll(Objects.requireNonNull(savedUsers));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll().block();
        storedDocuments.clear();
    }

    @Test
    void checkFindOnDemandFilterBySentenceTest() {
        var selectedRandomUser = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool()
                ? selectedRandomUser.name().substring(1, 2)
                : selectedRandomUser.email().substring(1, 2);
        var pageRequest = UserPageRequest.builder().sentence(sentence).build();
        StepVerifier.create(userRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var expectedSize = storedDocuments.stream()
                            .filter(u -> u.name().contains(sentence) || u.email().contains(sentence))
                            .count();
                    assertThat(actual.size()).isEqualTo(expectedSize);
                    var actualList = new ArrayList<>(actual);
                    assertThat(actualList).isSortedAccordingTo(Comparator.comparing(UserDocument::name));
                })
                .verifyComplete();
    }

    @Test
    void checkCountFilterBySentenceTest() {
        var selectedRandomUser = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool()
                ? selectedRandomUser.name().substring(1, 2)
                : selectedRandomUser.email().substring(1, 2);
        var pageRequest = UserPageRequest.builder().sentence(sentence).build();
        StepVerifier.create(userRepositoryImpl.count(pageRequest))
                .assertNext(actual -> {
                    var expectedSize = storedDocuments.stream()
                            .filter(u -> u.name().contains(sentence) || u.email().contains(sentence))
                            .count();
                    assertThat(actual).isEqualTo(expectedSize);
                })
                .verifyComplete();
    }

    @Test
    void combineAllOptions() {
        var selectedRandomUser = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool()
                ? selectedRandomUser.name().substring(1, 2)
                : selectedRandomUser.email().substring(1, 2);
        var pageRequest = UserPageRequestFactoryBot.builder().build()
                .toBuilder()
                .sentence(sentence)
                .limit(20)
                .page(0L)
                .build();
        StepVerifier.create(userRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> assertThat(actual).isNotEmpty())
                .verifyComplete();
    }

    private static Stream<Arguments> verifySort() {
        return Stream.of(
                Arguments.of(
                        UserPageRequest.builder().sortBy(UserSortBy.NAME).sortDirection(UserSortDirection.ASC).build(),
                        Comparator.comparing(UserDocument::name)
                ),
                Arguments.of(
                        UserPageRequest.builder().sortBy(UserSortBy.NAME).sortDirection(UserSortDirection.DESC).build(),
                        Comparator.comparing(UserDocument::name).reversed()
                ),
                Arguments.of(
                        UserPageRequest.builder().sortBy(UserSortBy.EMAIL).sortDirection(UserSortDirection.ASC).build(),
                        Comparator.comparing(UserDocument::email)
                ),
                Arguments.of(
                        UserPageRequest.builder().sortBy(UserSortBy.EMAIL).sortDirection(UserSortDirection.DESC).build(),
                        Comparator.comparing(UserDocument::email).reversed()
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    void verifySort(final UserPageRequest pageRequest, final Comparator<UserDocument> comparator) {
        StepVerifier.create(userRepositoryImpl.findOnDemand(pageRequest))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var actualList = new ArrayList<>(actual);
                    assertThat(actualList).isSortedAccordingTo(comparator);
                })
                .verifyComplete();
    }

}
