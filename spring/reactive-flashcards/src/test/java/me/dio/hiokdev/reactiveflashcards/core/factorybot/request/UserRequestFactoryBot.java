package me.dio.hiokdev.reactiveflashcards.core.factorybot.request;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserRequest;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequestFactoryBot {

    public static UserRequestFactoryBotBuilder builder() {
        return new UserRequestFactoryBotBuilder();
    }

    public static class UserRequestFactoryBotBuilder {

        private String name;
        private String email;
        private final Faker faker = RandomData.getFaker();

        public UserRequestFactoryBotBuilder() {
            this.name = faker.name().name();
            this.email = faker.internet().emailAddress();
        }

        public UserRequestFactoryBotBuilder blankName() {
            this.name = faker.bool().bool() ? null : " ";
            return this;
        }

        public UserRequestFactoryBotBuilder longName() {
            this.name = faker.lorem().sentence(256);
            return this;
        }

        public UserRequestFactoryBotBuilder blankEmail() {
            this.email = faker.bool().bool() ? null : " ";
            return this;
        }

        public UserRequestFactoryBotBuilder longEmail() {
            this.email = faker.lorem().sentence(256);
            return this;
        }

        public UserRequestFactoryBotBuilder invalidEmail() {
            this.email = faker.lorem().word();
            return this;
        }

        public UserRequest build() {
            return UserRequest.builder()
                    .name(name)
                    .email(email)
                    .build();
        }

    }

}
