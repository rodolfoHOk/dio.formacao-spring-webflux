package me.dio.hiokdev.reactiveflashcards.domain.mapper;

import me.dio.hiokdev.reactiveflashcards.domain.document.Card;
import me.dio.hiokdev.reactiveflashcards.domain.document.Question;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyCard;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.QuestionDTO;
import me.dio.hiokdev.reactiveflashcards.domain.dto.StudyCardDTO;
import me.dio.hiokdev.reactiveflashcards.domain.dto.StudyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudyDomainMapper {

    StudyCard toStudyCard(final Card card);

    @Mapping(target = "asked", source = "front")
    @Mapping(target = "answered", ignore = true)
    @Mapping(target = "expected", source = "back")
    Question toQuestion(final StudyCard studyCard);

    StudyDTO toDTO(final StudyDocument studyDocument, final List<String> remainAsks);

    @Mapping(target = "asked", source = "front")
    @Mapping(target = "answered", ignore = true)
    @Mapping(target = "expected", source = "back")
    QuestionDTO toDTO(final StudyCardDTO studyCard);

    StudyDocument toDocument(final StudyDTO studyDTO);

}
