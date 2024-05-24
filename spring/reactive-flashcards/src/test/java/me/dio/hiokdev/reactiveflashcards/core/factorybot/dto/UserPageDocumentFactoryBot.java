package me.dio.hiokdev.reactiveflashcards.core.factorybot.dto;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.UserDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.UserPageDocument;

import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPageDocumentFactoryBot {

    public static UserPageDocumentFactoryBotBuilder builder() {
        return new UserPageDocumentFactoryBotBuilder();
    }

    public static class UserPageDocumentFactoryBotBuilder {

        private Long currentPage;
        private Integer limit;
        private Long totalItens;
        private List<UserDocument> content;
        private final Faker faker = RandomData.getFaker();

        public UserPageDocumentFactoryBotBuilder() {
            this.currentPage = faker.number().numberBetween(1L, 20L);
            this.limit = faker.number().numberBetween(1, 10);
            this.content = Stream.generate(() -> UserDocumentFactoryBot.builder().build())
                    .limit(limit)
                    .toList();
            this.totalItens = faker.number().numberBetween(content.size(), content.size() * 3L);
        }

        public UserPageDocumentFactoryBotBuilder emptyPage() {
            this.currentPage = 0L;
            this.limit = faker.number().numberBetween(5, 20);
            this.totalItens = 0L;
            this.content = List.of();
            return this;
        }

        public UserPageDocument build() {
            return UserPageDocument.builder()
                    .currentPage(this.currentPage)
                    .limit(this.limit)
                    .totalItens(this.totalItens)
                    .content(this.content)
                    .build();
        }

    }
}
