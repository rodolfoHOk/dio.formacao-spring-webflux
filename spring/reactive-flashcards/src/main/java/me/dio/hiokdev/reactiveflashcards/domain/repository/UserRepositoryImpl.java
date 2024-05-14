package me.dio.hiokdev.reactiveflashcards.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
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
                .zipWhen(query -> buildWhere(request.sentence()))
                .map(tuple -> {
                    var whereClause = new Criteria();
                    whereClause.orOperator(tuple.getT2());
                    return tuple.getT1().addCriteria(whereClause);
                })
                .map(query -> query.with(request.getSort()).skip(request.getSkip()).limit(request.limit()))
                .doFirst(() -> log.info("==== Find users on demand with follow request {}", request))
                .flatMapMany(query -> template.find(query, UserDocument.class));
    }

    public Mono<Long> count(final UserPageRequest request) {
        return Mono.just(new Query())
                .zipWhen(query -> buildWhere(request.sentence()))
                .map(tuple -> {
                    var whereClause = new Criteria();
                    whereClause.orOperator(tuple.getT2());
                    return tuple.getT1().addCriteria(whereClause);
                })
                .doFirst(() -> log.info("==== Counting users with follow request {}", request))
                .flatMap(query -> template.count(query, UserDocument.class));
    }

    private Mono<List<Criteria>> buildWhere(final String sentence) {
        return Flux.fromIterable(List.of("name", "email"))
                .map(field -> Criteria.where(field).regex(sentence, "i"))
                .collectList();
    }

}
