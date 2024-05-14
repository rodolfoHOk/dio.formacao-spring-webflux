package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public record UserPageRequest(
        @JsonProperty("sentence") String sentence,
        @JsonProperty("page") @PositiveOrZero Long page,
        @JsonProperty("limit") @Min(1) @Max(50) Integer limit,
        @JsonProperty("sortBy") UserSortBy sortBy,
        @JsonProperty("sortDirection") UserSortDirection sortDirection
) {

    @Builder(toBuilder = true)
    public UserPageRequest {
        if (Objects.isNull(sortBy)) sortBy = UserSortBy.Name;
        if (Objects.isNull(sortDirection)) sortDirection = UserSortDirection.ASC;
        if (Objects.isNull(limit)) limit = 20;
        if (Objects.isNull(page)) page = 0L;
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
