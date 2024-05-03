package me.dio.hiokdev.reactiveflashcards.domain.document;

import lombok.Builder;
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
        @CreatedDate @Field("created_at") OffsetDateTime createdAt,
        @LastModifiedDate @Field("updated_at") OffsetDateTime updatedAt
) {

    @Builder(toBuilder = true)
    public StudyDocument {}

    public StudyDocument addQuestion(final Question question) {
        var newQuestions = new ArrayList<Question>();
        if (this.questions != null && !this.questions.isEmpty()) {
            newQuestions.addAll(this.questions);
        }
        newQuestions.add(question);
        return this.toBuilder().questions(newQuestions).build();
    }

    public Question getLastPendingQuestion() {
        return this.questions.stream()
                .filter(question -> Objects.isNull(question.answeredIn())).findFirst().orElseThrow();
    }

}
