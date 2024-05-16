package me.dio.hiokdev.reactiveflashcards.core.factorybot.document;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDeck;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyDocumentFactoryBot {

    public static StudyDocumentFactoryBotBuilder builder(final String userId, DeckDocument deck) {
        return new StudyDocumentFactoryBotBuilder(userId, deck);
    }

    public static class StudyDocumentFactoryBotBuilder {

        private String id;
        private String userId;
        private StudyDeck studyDeck;
        private final List<Question> questions = new ArrayList<>();
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private final Faker faker = RandomData.getFaker();

        public StudyDocumentFactoryBotBuilder(final String userId, DeckDocument deck) {
            this.id = ObjectId.get().toString();
            this.userId = userId;
            this.studyDeck = generateStudyDeck(deck);
            generateQuestions();
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        public StudyDocument build() {
            return StudyDocument.builder()
                    .id(id)
                    .userId(userId)
                    .studyDeck(studyDeck)
                    .questions(questions)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

        private StudyDeck generateStudyDeck(final DeckDocument deck) {
            // TODO studyCards from deck cards and add in builder
            return StudyDeck.builder().deckId(deck.id()).build();
        }

        private void generateQuestions() {
            // TODO generateQuestions
        }

    }

}
