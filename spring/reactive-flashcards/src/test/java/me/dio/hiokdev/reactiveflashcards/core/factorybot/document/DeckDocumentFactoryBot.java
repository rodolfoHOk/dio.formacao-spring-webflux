package me.dio.hiokdev.reactiveflashcards.core.factorybot.document;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.domain.document.Card;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeckDocumentFactoryBot {

    public static DeckDocumentFactoryBotBuilder builder() {
        return new DeckDocumentFactoryBotBuilder();
    }

    public static class DeckDocumentFactoryBotBuilder {

        private String id;
        private String name;
        private String description;
        private final Set<Card> cards = new HashSet<>();
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private final Faker faker = RandomData.getFaker();

        public DeckDocumentFactoryBotBuilder() {
            this.id = ObjectId.get().toString();
            this.name = faker.name().name();
            this.description = faker.yoda().quote();
            generateCards();
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        public DeckDocumentFactoryBotBuilder preInsert() {
            this.id = null;
            this.createdAt = null;
            this.updatedAt = null;
            return this;
        }

        public DeckDocumentFactoryBotBuilder preUpdate(final String id) {
            this.id = id;
            return this;
        }

        public DeckDocument build() {
            return DeckDocument.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .cards(cards)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

        private void generateCards() {
            var amount = faker.number().numberBetween(3, 8);
            Set<String> front = new HashSet<>();
            while (front.size() != amount){
                front.add(faker.cat().name());
            }
            Set<String> back = new HashSet<>();
            while (back.size() != amount){
                back.add(faker.color().name());
            }
            var frontList = front.stream().toList();
            var backList = back.stream().toList();
            for (int i = 0; i < frontList.size(); i++) {
                cards.add(Card.builder()
                        .front(frontList.get(i))
                        .back(backList.get(i))
                        .build());
            }
        }

    }

}
