package me.dio.hiokdev.reactiveflashcards.domain.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;

import java.util.List;

public record UserPageDocument(
        Long currentPage,
        Long totalPages,
        Long totalItens,
        List<UserDocument> content
) {

    public static UserPageDocumentBuilder builder() {
        return new UserPageDocumentBuilder();
    }

    public UserPageDocumentBuilder toBuilder(final Integer limit) {
        return new UserPageDocumentBuilder(limit, currentPage, totalItens, content);
    }

    public static UserPageDocument emptyPage() {
        return new UserPageDocument(0L, 0L, 0L, List.of());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserPageDocumentBuilder {

        private Integer limit;
        private Long currentPage;
        private Long totalItens;
        private List<UserDocument> content;

        public UserPageDocumentBuilder limit(final Integer limit) {
            this.limit = limit;
            return this;
        }

        public UserPageDocumentBuilder currentPage(final Long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public UserPageDocumentBuilder totalItens(final Long totalItens) {
            this.totalItens = totalItens;
            return this;
        }

        public UserPageDocumentBuilder content(final List<UserDocument> content) {
            this.content = content;
            return this;
        }

        public UserPageDocument build() {
            var totalPages = (totalItens / limit) + ((totalItens % limit > 0) ? 1 : 0);
            return new UserPageDocument(currentPage, totalPages, totalItens, content);
        }

    }

}
