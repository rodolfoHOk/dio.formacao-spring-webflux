package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record UserRequest(
        @JsonProperty("name") @NotBlank @Size(min = 3, max = 255) String name,
        @JsonProperty("email") @NotBlank @Size(min = 3, max = 255) @Email String email
) {

    @Builder(toBuilder = true)
    public UserRequest {}

}
