package me.dio.hiokdev.reactiveflashcards.domain.document;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

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

}
