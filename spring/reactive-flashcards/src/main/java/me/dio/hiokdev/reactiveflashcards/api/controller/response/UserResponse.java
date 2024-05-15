package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record UserResponse(
        @JsonProperty("id")
        @Schema(description = "Identificador do usuário", example = "66342b8418c87a1a8a8ffcb0")
        String id,

        @JsonProperty("name")
        @Schema(description = "Nome do usuário", example = "João Empreendedor")
        String name,

        @JsonProperty("email")
        @Schema(description = "E-mail do usuário", example = "joao@empresa.com.br")
        String email
) {

    @Builder(toBuilder = true)
    public UserResponse {}

}
