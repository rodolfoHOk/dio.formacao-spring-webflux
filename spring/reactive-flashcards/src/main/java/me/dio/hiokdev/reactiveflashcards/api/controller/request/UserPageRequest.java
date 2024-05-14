package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;

public record UserPageRequest(
        @JsonProperty("sentence") String sentence,
        @JsonProperty("page") @PositiveOrZero Long page,
        @JsonProperty("limit") @Min(1) @Max(50) Integer limit,
        @JsonProperty("sortBy") UserSortBy sortBy,
        @JsonProperty("sortDirection") UserSortDirection sortDirection
) {

    @Builder(toBuilder = true)
    public UserPageRequest {
        sentence = ObjectUtils.defaultIfNull(sentence, "");
        sortBy = ObjectUtils.defaultIfNull(sortBy, UserSortBy.NAME);
        sortDirection = ObjectUtils.defaultIfNull(sortDirection, UserSortDirection.ASC);
        limit = ObjectUtils.defaultIfNull(limit, 20);
        page = ObjectUtils.defaultIfNull(page, 0L);
    }

    public Sort getSort() {
        return sortDirection.equals(UserSortDirection.DESC)
                ? Sort.by(sortBy.getField()).descending()
                : Sort.by(sortBy.getField()).ascending();
    }

    public Long getSkip() {
        return page > 0 ? (page - 1) * limit : 0;
    }

}
