package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public record UserPageRequest(
        String sentence,
        Long page,
        Integer limit,
        UserSortBy sortBy,
        UserSortDirection sortDirection
) {

    @Builder(toBuilder = true)
    public UserPageRequest {
        if (Objects.isNull(sortBy)) sortBy = UserSortBy.Name;
        if (Objects.isNull(sortDirection)) sortDirection = UserSortDirection.ASC;
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
