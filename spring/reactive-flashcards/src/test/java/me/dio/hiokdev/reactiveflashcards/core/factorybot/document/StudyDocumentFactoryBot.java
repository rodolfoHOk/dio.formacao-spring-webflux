package me.dio.hiokdev.reactiveflashcards.core.factorybot.document;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyCard;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDeck;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

        public StudyDocumentFactoryBotBuilder pendingQuestions(final Integer remain) {
            this.questions.clear();
            studyDeck.cards().forEach(card -> {
                questions.add(Question.builder()
                        .asked(card.front())
                        .expected(card.back())
                        .answered(card.back())
                        .build());
            });
            var index = questions.size() - remain;
            while (index < questions.size()) {
                var selectedQuestion = questions.get(index);
                selectedQuestion = Question.builder()
                        .asked(selectedQuestion.asked())
                        .expected(selectedQuestion.expected())
                        .build();
                questions.set(index, selectedQuestion);
                ++index;
            }
            var positionsToRemove = remain - 1;
            while (positionsToRemove != 0){
                questions.remove(questions.size() - positionsToRemove);
                --positionsToRemove;
            }
            return this;
        }

        public StudyDocumentFactoryBotBuilder finishedStudy() {
            this.questions.clear();
            studyDeck.cards().forEach(card -> {
                questions.add(Question.builder()
                        .asked(card.front())
                        .expected(card.back())
                        .answered(card.back())
                        .build());
            });
            return this;
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
            var studyCards = deck.cards().stream()
                    .map(card -> StudyCard.builder()
                            .front(card.front())
                            .back(card.back())
                            .build())
                    .collect(Collectors.toSet());
            return StudyDeck.builder()
                    .deckId(deck.id())
                    .cards(studyCards)
                    .build();
        }

        private void generateQuestions() {
            generateRandomQuestionWithWrongAnswer();
            generateRandomQuestionWithRightAnswer();
            generateNonAskedRandomQuestion();
        }

        private void generateRandomQuestionWithWrongAnswer() {
            var values = new ArrayList<>(studyDeck.cards());
            var random = new Random();
            var position = random.nextInt(values.size());
            var card = values.get(position);
            questions.add(Question.builder()
                    .asked(card.front())
                    .answered(faker.app().name())
                    .expected(card.back())
                    .build());
        }

        private void generateRandomQuestionWithRightAnswer() {
            var values = new ArrayList<>(studyDeck.cards());
            var random = new Random();
            var position = random.nextInt(values.size());
            var card = values.get(position);
            questions.add(Question.builder()
                    .asked(card.front())
                    .answered(card.back())
                    .expected(card.back())
                    .build());
        }

        private void generateNonAskedRandomQuestion() {
            var values = new ArrayList<>(studyDeck.cards());
            var random = new Random();
            var position = random.nextInt(values.size());
            var card = values.get(position);
            questions.add(Question.builder()
                    .asked(card.front())
                    .expected(card.back())
                    .build());
        }

    }

}
