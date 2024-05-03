package me.dio.hiokdev.reactiveflashcards.domain.document;

public record StudyCard(
        String front,
        String back
) {

    public static StudyCardBuilder builder(){
        return new StudyCardBuilder();
    }

    public StudyCardBuilder toBuilder(){
        return new StudyCardBuilder(front, back);
    }

    public static class StudyCardBuilder {

        private String front;
        private String back;

        public StudyCardBuilder() { }

        public StudyCardBuilder(String front, String back) {
            this.front = front;
            this.back = back;
        }

        public StudyCardBuilder front(final String front) {
            this.front = front;
            return this;
        }

        public StudyCardBuilder back(final String back) {
            this.back = back;
            return this;
        }

        public StudyCard build() {
            return new StudyCard(front, back);
        }

    }

}
