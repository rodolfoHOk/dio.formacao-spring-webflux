package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserSortBy {

    Name("name"),
    Email("email");

    private final String field;

}
