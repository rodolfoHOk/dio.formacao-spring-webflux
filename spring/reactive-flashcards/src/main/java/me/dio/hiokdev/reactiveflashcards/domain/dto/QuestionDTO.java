package me.dio.hiokdev.reactiveflashcards.domain.dto;

import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.Objects;

public record QuestionDTO(
        String asked,
        OffsetDateTime askedIn,
        String answered,
        OffsetDateTime answeredIn,
        String expected
) {

    public static QuestionDTOBuilder builder() {
        return new QuestionDTOBuilder();
    }

    public QuestionDTOBuilder toBuilder() {
        return new QuestionDTOBuilder(asked, askedIn, answered, answeredIn, expected);
    }

    public Boolean isNotAnswered() {
        return Objects.isNull(answeredIn);
    }

    public Boolean isCorrect() {
        return Objects.nonNull(answeredIn) && answered.equals(expected);
    }

    public static class QuestionDTOBuilder {

        private String asked;
        private OffsetDateTime askedIn;
        private String answered;
        private OffsetDateTime answeredIn;
        private String expected;

        public QuestionDTOBuilder() { }

        public QuestionDTOBuilder(
                String asked,
                OffsetDateTime askedIn,
                String answered,
                OffsetDateTime answeredIn,
                String expected
        ) {
            this.asked = asked;
            this.askedIn = askedIn;
            this.answered = answered;
            this.answeredIn = answeredIn;
            this.expected = expected;
        }

        public QuestionDTOBuilder asked(final String asked) {
            if (StringUtils.isNotBlank(asked)) {
                this.asked = asked;
                this.askedIn = OffsetDateTime.now();
            }
            return this;
        }

        public QuestionDTOBuilder answered(final String answered) {
            if (StringUtils.isNotBlank(answered)) {
                this.answered = answered;
                this.answeredIn = OffsetDateTime.now();
            }
            return this;
        }

        public QuestionDTOBuilder expected(final String expected) {
            this.expected = expected;
            return this;
        }

        public QuestionDTO build() {
            return new QuestionDTO(asked, askedIn, answered, answeredIn, expected);
        }

    }

}
