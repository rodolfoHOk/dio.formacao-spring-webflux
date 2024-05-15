package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record UserRequest(
        @JsonProperty("name")
        @NotBlank
        @Size(min = 3, max = 255)
        @Schema(description = "Nome do usuário", example = "João Empreendedor")
        String name,

        @JsonProperty("email")
        @NotBlank
        @Size(min = 3, max = 255)
        @Email
        @Schema(description = "E-mail do usuário", example = "joao@empresa.com.br")
        String email
) {

    @Builder(toBuilder = true)
    public UserRequest {}

}
