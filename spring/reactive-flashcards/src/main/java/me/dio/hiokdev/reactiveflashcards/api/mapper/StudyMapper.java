package me.dio.hiokdev.reactiveflashcards.api.mapper;

import me.dio.hiokdev.reactiveflashcards.api.controller.request.StudyRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.QuestionResponse;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "studyDeck.deckId", source = "deckId")
    @Mapping(target = "studyDeck.cards", ignore = true)
    @Mapping(target = "questions", ignore = true)
    StudyDocument toDocument(final StudyRequest request);

    QuestionResponse toResponse(final Question question);

}
