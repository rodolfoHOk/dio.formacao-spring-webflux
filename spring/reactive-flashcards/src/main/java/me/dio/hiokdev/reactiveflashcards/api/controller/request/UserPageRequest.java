package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;

public record UserPageRequest(
        @JsonProperty("sentence")
        @Schema(description = "Texto para filtrar por nome ou e-mail (case insensitive)", example = "ana")
        String sentence,

        @JsonProperty("page")
        @PositiveOrZero
        @Schema(description = "Pagina solicitada", example = "1", defaultValue = "0")
        Long page,

        @JsonProperty("limit")
        @Min(1)
        @Max(50)
        @Schema(description = "Tamanho da página", example = "30", defaultValue = "20")
        Integer limit,

        @JsonProperty("sortBy")
        @Schema(description = "Campo para ordenação", enumAsRef = true, defaultValue = "NAME")
        UserSortBy sortBy,

        @JsonProperty("sortDirection")
        @Schema(description = "Sentido da ordenação", enumAsRef = true, defaultValue = "ASC")
        UserSortDirection sortDirection
) {

    @Builder(toBuilder = true)
    public UserPageRequest {
        sentence = ObjectUtils.defaultIfNull(sentence, "");
        sortBy = ObjectUtils.defaultIfNull(sortBy, UserSortBy.NAME);
        sortDirection = ObjectUtils.defaultIfNull(sortDirection, UserSortDirection.ASC);
        limit = ObjectUtils.defaultIfNull(limit, 20);
        page = ObjectUtils.defaultIfNull(page, 0L);
    }

    @Schema(hidden = true)
    public Sort getSort() {
        return sortDirection.equals(UserSortDirection.DESC)
                ? Sort.by(sortBy.getField()).descending()
                : Sort.by(sortBy.getField()).ascending();
    }

    @Schema(hidden = true)
    public Long getSkip() {
        return page > 0 ? (page - 1) * limit : 0;
    }

}
