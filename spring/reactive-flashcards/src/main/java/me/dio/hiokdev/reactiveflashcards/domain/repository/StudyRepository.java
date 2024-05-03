package me.dio.hiokdev.reactiveflashcards.domain.repository;

import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StudyRepository extends ReactiveMongoRepository<StudyDocument, String> {

    Mono<StudyDocument> findByUserIdAndStudyDeck_DeckIdAndCompletedFalse(final String userId, final String deckId);

}
