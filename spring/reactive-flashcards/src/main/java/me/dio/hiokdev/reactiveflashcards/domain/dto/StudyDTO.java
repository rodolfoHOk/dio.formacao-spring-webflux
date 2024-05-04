package me.dio.hiokdev.reactiveflashcards.domain.dto;

import org.apache.commons.collections4.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public record StudyDTO(
        String id,
        String userId,
        StudyDeckDTO studyDeck,
        List<QuestionDTO> questions,
        List<String> remainAsks,
        Boolean completed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static StudyDTOBuilder builder() {
        return new StudyDTOBuilder();
    }

    public StudyDTOBuilder toBuilder() {
        return new StudyDTOBuilder(id, userId, studyDeck, questions, remainAsks, createdAt, updatedAt);
    }

    public Boolean hasAnyAnswer() {
        return CollectionUtils.isNotEmpty(remainAsks);
    }

    public static class StudyDTOBuilder {

        private String id;
        private String userId;
        private StudyDeckDTO studyDeck;
        private List<QuestionDTO> questions = new ArrayList<>();
        private List<String> remainAsks = new ArrayList<>();
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public StudyDTOBuilder() { }

        public StudyDTOBuilder(
                String id,
                String userId,
                StudyDeckDTO studyDeck,
                List<QuestionDTO> questions,
                List<String> remainAsks,
                OffsetDateTime createdAt,
                OffsetDateTime updatedAt
        ) {
            this.id = id;
            this.userId = userId;
            this.studyDeck = studyDeck;
            this.questions = questions;
            this.remainAsks = remainAsks;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public StudyDTOBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public StudyDTOBuilder userId(final String userId) {
            this.userId = userId;
            return this;
        }

        public StudyDTOBuilder studyDeck(final StudyDeckDTO studyDeck) {
            this.studyDeck = studyDeck;
            return this;
        }

        public StudyDTOBuilder questions(final List<QuestionDTO> questions) {
            this.questions = questions;
            return this;
        }

        public StudyDTOBuilder question(QuestionDTO question) {
            this.questions.add(question);
            return this;
        }

        public StudyDTOBuilder remainAsks(final List<String> remainAsks) {
            this.remainAsks = remainAsks;
            return this;
        }

        public StudyDTOBuilder createdAt(final OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudyDTOBuilder updatedAt(final OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public StudyDTO build() {
            var rightQuestions = questions.stream().filter(QuestionDTO::isCorrect).toList();
            var completed = rightQuestions.size() == studyDeck.cards().size();
            return new StudyDTO(id, userId, studyDeck, questions, remainAsks, completed, createdAt, updatedAt);
        }

    }

}
