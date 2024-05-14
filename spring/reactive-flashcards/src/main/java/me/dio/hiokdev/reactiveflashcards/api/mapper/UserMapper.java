package me.dio.hiokdev.reactiveflashcards.api.mapper;

import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserPageResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserResponse;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.UserPageDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserDocument toDocument(final UserRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserDocument toDocument(final UserRequest request, final String id);

    UserResponse toResponse(final UserDocument document);

    UserPageResponse toResponse(final UserPageDocument userPageDocument, final Integer limit);
}
