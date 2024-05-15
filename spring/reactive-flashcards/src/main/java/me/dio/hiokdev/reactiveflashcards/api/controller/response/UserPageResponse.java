package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public record UserPageResponse(
        @JsonProperty("currentPage")
        @Schema(description = "Pagina retornada", example = "1")
        Long currentPage,

        @JsonProperty("totalPages")
        @Schema(description = "Total de páginas", example = "20")
        Long totalPages,

        @JsonProperty("totalItens")
        @Schema(description = "Quantidade de registros paginados", example = "100")
        Long totalItens,

        @JsonProperty("content")
        @Schema(description = "Dados dos usuários da página")
        List<UserResponse> content
) {

    public static UserPageResponseBuilder builder() {
        return new UserPageResponseBuilder();
    }

    public UserPageResponseBuilder toBuilder(final Integer limit) {
        return new UserPageResponseBuilder(limit, currentPage, totalItens, content);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserPageResponseBuilder {

        private Integer limit;
        private Long currentPage;
        private Long totalItens;
        private List<UserResponse> content;

        public UserPageResponseBuilder limit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public UserPageResponseBuilder currentPage(final Long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public UserPageResponseBuilder totalItens(final Long totalItens) {
            this.totalItens = totalItens;
            return this;
        }

        public UserPageResponseBuilder content(final List<UserResponse> content) {
            this.content = content;
            return this;
        }

        public UserPageResponse build() {
            var totalPages = (totalItens / limit) + ((totalItens % limit > 0) ? 1 : 0);
            return new UserPageResponse(currentPage, totalPages, totalItens, content);
        }

    }

}
