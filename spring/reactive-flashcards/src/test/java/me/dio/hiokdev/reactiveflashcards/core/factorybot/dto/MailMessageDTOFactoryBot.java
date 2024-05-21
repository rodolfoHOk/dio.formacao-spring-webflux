package me.dio.hiokdev.reactiveflashcards.core.factorybot.dto;

import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.dto.MailMessageDTO;

import java.util.List;

public class MailMessageDTOFactoryBot {

    public static MailMessageDTOFactoryBotBuilder builder(final DeckDocument deck, final List<Question> questions){
        return new MailMessageDTOFactoryBotBuilder(deck, questions);
    }

    public static class MailMessageDTOFactoryBotBuilder {

        private String destination;
        private String subject;
        private String username;
        private final DeckDocument deck;
        private final List<Question> questions;

        public MailMessageDTOFactoryBotBuilder(final DeckDocument deck, final List<Question> questions) {
            var faker = RandomData.getFaker();
            this.destination = faker.internet().emailAddress();
            this.subject = faker.chuckNorris().fact();
            this.username = faker.name().fullName();
            this.deck = deck;
            this.questions = questions;
        }

        public MailMessageDTO build() {
            return MailMessageDTO.builder()
                    .destination(destination)
                    .subject(subject)
                    .template("mail/studyResult")
                    .username(username)
                    .deck(deck)
                    .questions(questions)
                    .build();
        }

    }

}
