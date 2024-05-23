package me.dio.hiokdev.reactiveflashcards.core.factorybot.request;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.StudyRequest;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import org.bson.types.ObjectId;

public class StudyRequestFactoryBot {

    public static StudyRequestFactoryBotBuilder builder(){
        return new StudyRequestFactoryBotBuilder();
    }

    public static class StudyRequestFactoryBotBuilder {

        private String userId;
        private String deckId;
        private final Faker faker = RandomData.getFaker();

        public StudyRequestFactoryBotBuilder() {
            this.userId = ObjectId.get().toString();
            this.deckId = ObjectId.get().toString();
        }

        public StudyRequestFactoryBotBuilder invalidUserId(){
            this.userId = faker.bool().bool() ? null : faker.lorem().word();
            return this;
        }

        public StudyRequestFactoryBotBuilder invalidDeckId(){
            this.deckId = faker.bool().bool() ? null : faker.lorem().word();
            return this;
        }

        public StudyRequest build() {
            return StudyRequest.builder()
                    .userId(userId)
                    .deckId(deckId)
                    .build();
        }

    }

}
