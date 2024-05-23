package me.dio.hiokdev.reactiveflashcards.core.factorybot.request;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.AnswerQuestionRequest;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;

public class AnswerQuestionRequestFactoryBot {

    public static AnswerQuestionRequestFactoryBotBuilder builder(){
        return new AnswerQuestionRequestFactoryBotBuilder();
    }

    public static class AnswerQuestionRequestFactoryBotBuilder {

        private String answer;
        private final Faker faker = RandomData.getFaker();

        public AnswerQuestionRequestFactoryBotBuilder() {
            this.answer = faker.lorem().word();
        }

        public AnswerQuestionRequestFactoryBotBuilder blankAnswer(){
            this.answer = faker.bool().bool() ? null : " ";
            return this;
        }

        public AnswerQuestionRequestFactoryBotBuilder longAnswer(){
            this.answer = faker.lorem().sentence(256);
            return this;
        }

        public AnswerQuestionRequest build(){
            return AnswerQuestionRequest.builder()
                    .answer(answer)
                    .build();
        }

    }

}
