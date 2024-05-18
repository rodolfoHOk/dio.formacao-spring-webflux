package me.dio.hiokdev.reactiveflashcards.core.factorybot.request;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserSortBy;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserSortDirection;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;

public class UserPageRequestFactoryBot {

    public static UserPageRequestFactoryBotBuilder builder(){
        return new UserPageRequestFactoryBotBuilder();
    }

    public static class UserPageRequestFactoryBotBuilder {

        private final String sentence;
        private final Long page;
        private final Integer limit;
        private final UserSortBy sortBy;
        private final UserSortDirection sortDirection;
        private final Faker faker = RandomData.getFaker();

        public UserPageRequestFactoryBotBuilder() {
            this.sentence = faker.lorem().sentence();
            this.page = faker.number().numberBetween(0L, 3L);
            this.limit = faker.number().numberBetween(20, 40);
            this.sortBy = RandomData.randomEnum(UserSortBy.class);
            this.sortDirection = RandomData.randomEnum(UserSortDirection.class);
        }

        public UserPageRequest build(){
            return UserPageRequest.builder()
                    .sentence(sentence)
                    .page(page)
                    .limit(limit)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
        }

    }

}
