package me.dio.hiokdev.reactiveflashcards.domain.document;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

public record Question(
        String asked,
        @Field("asked_in") OffsetDateTime askedIn,
        String answered,
        @Field("answered_in") OffsetDateTime answeredIn,
        String expected
) {

    public static QuestionBuilder builder(){
        return new QuestionBuilder();
    }

    public QuestionBuilder toBuilder(){
        return new QuestionBuilder(asked, askedIn, answered, answeredIn, expected);
    }

    public static class QuestionBuilder {

        private String asked;
        private OffsetDateTime askedIn;
        private String answered;
        private OffsetDateTime answeredIn;
        private String expected;

        public QuestionBuilder() { }

        public QuestionBuilder(
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

        public QuestionBuilder asked(final String asked) {
            if (StringUtils.isNotBlank(asked)) {
                this.asked = asked;
                this.askedIn = OffsetDateTime.now();
            }
            return this;
        }

        public QuestionBuilder answered(final String answered) {
            if (StringUtils.isNotBlank(answered)) {
                this.answered = answered;
                this.answeredIn = OffsetDateTime.now();
            }
            return this;
        }

        public QuestionBuilder expected(final String expected) {
            this.expected = expected;
            return this;
        }

        public Question build() {
            return new Question(asked, askedIn, answered, answeredIn, expected);
        }

    }

}
