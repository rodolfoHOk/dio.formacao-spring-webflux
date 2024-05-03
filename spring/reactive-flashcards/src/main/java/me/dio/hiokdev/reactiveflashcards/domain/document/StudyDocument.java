package me.dio.hiokdev.reactiveflashcards.domain.document;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "studies")
public record StudyDocument(
        @Id String id,
        @Field("user_id") String userId,
        @Field("study_deck") StudyDeck studyDeck,
        List<Question> questions,
        Boolean completed,
        @CreatedDate @Field("created_at") OffsetDateTime createdAt,
        @LastModifiedDate @Field("updated_at") OffsetDateTime updatedAt
) {

    public static StudyDocumentBuilder builder() {
        return new StudyDocumentBuilder();
    }

    public StudyDocumentBuilder toBuilder() {
        return new StudyDocumentBuilder(id, userId, studyDeck, questions, completed, createdAt, updatedAt);
    }

    public Question getLastPendingQuestion() {
        return this.questions.stream()
                .filter(question -> Objects.isNull(question.answeredIn())).findFirst().orElseThrow();
    }

    public static class StudyDocumentBuilder {

        private String id;
        private String userId;
        private StudyDeck studyDeck;
        private List<Question> questions = new ArrayList<>();
        private Boolean completed = false;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public StudyDocumentBuilder() { }

        public StudyDocumentBuilder(
                String id,
                String userId,
                StudyDeck studyDeck,
                List<Question> questions,
                Boolean completed,
                OffsetDateTime createdAt,
                OffsetDateTime updatedAt
        ) {
            this.id = id;
            this.userId = userId;
            this.studyDeck = studyDeck;
            this.questions = questions;
            this.completed = completed;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public StudyDocumentBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public StudyDocumentBuilder userId(final String userId) {
            this.userId = userId;
            return this;
        }

        public StudyDocumentBuilder studyDeck(final StudyDeck studyDeck) {
            this.studyDeck = studyDeck;
            return this;
        }

        public StudyDocumentBuilder questions(final List<Question> questions) {
            this.questions = questions;
            return this;
        }

        public StudyDocumentBuilder question(Question question) {
            this.questions.add(question);
            return this;
        }

        public StudyDocumentBuilder completed(Boolean completed) {
            this.completed = completed;
            return this;
        }

        public StudyDocumentBuilder createdAt(final OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudyDocumentBuilder updatedAt(final OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public StudyDocument build() {
            return new StudyDocument(id, userId, studyDeck, questions, completed, createdAt, updatedAt);
        }

    }

}
