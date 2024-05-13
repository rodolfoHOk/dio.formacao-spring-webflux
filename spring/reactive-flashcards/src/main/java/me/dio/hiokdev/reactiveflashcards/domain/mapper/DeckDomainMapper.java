package me.dio.hiokdev.reactiveflashcards.domain.mapper;

import me.dio.hiokdev.reactiveflashcards.domain.document.Card;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.CardRestDTO;
import me.dio.hiokdev.reactiveflashcards.domain.dto.DeckRestDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DeckDomainMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "info")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DeckDocument toDocument(final DeckRestDTO dto);

    @Mapping(target = "front", source = "ask")
    @Mapping(target = "back", source = "answer")
    Card toDocument(final CardRestDTO dto);

}
