package me.dio.hiokdev.reactiveflashcards.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl {

    private final ReactiveMongoTemplate template;

    public Flux<UserDocument> findOnDemand(final UserPageRequest request) {
        return Mono.just(new Query())
                .flatMap(query -> buildWhere(query, request.sentence()))
                .map(query -> query.with(request.getSort()).skip(request.getSkip()).limit(request.limit()))
                .doFirst(() -> log.info("==== Find users on demand with follow request {}", request))
                .flatMapMany(query -> template.find(query, UserDocument.class));
    }

    public Mono<Long> count(final UserPageRequest request) {
        return Mono.just(new Query())
                .flatMap(query -> buildWhere(query, request.sentence()))
                .doFirst(() -> log.info("==== Counting users with follow request {}", request))
                .flatMap(query -> template.count(query, UserDocument.class));
    }

    private Mono<Query> buildWhere(final Query query, final String sentence) {
        return Mono.just(query)
                .filter(q -> StringUtils.isNoneBlank(sentence))
                .switchIfEmpty(Mono.defer(() -> Mono.just(query))
                        .flatMapIterable(q -> List.of("name", "email"))
                        .map(field -> Criteria.where(field).regex(sentence, "i"))
                        .collectList()
                        .map(criteriaList -> setWhereClause(query, criteriaList)));
    }

    private Query setWhereClause(final Query query, final List<Criteria> criteriaList) {
        var whereClause = new Criteria();
        whereClause.orOperator(criteriaList);
        return query.addCriteria(whereClause);
    }

}
