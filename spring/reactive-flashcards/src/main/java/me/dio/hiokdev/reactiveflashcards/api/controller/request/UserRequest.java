package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record UserRequest(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email
) {

    @Builder(toBuilder = true)
    public UserRequest {}

}
