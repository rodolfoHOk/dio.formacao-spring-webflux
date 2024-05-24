package me.dio.hiokdev.reactiveflashcards.core.factorybot.dto;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.domain.dto.CardRestDTO;
import me.dio.hiokdev.reactiveflashcards.domain.dto.DeckRestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeckRestDTOFactoryBot {

    public static DeckRestDTOFactoryBotBuilder builder(){
        return new DeckRestDTOFactoryBotBuilder();
    }

    public static class DeckRestDTOFactoryBotBuilder {

        private final String name;
        private final String info;
        private final String author;
        private final List<CardRestDTO> cards = new ArrayList<>();
        private final Faker faker = RandomData.getFaker();

        public DeckRestDTOFactoryBotBuilder() {
            this.name = faker.name().name();
            this.info = faker.chuckNorris().fact();
            this.author = faker.name().fullName();
            generateCards();
        }

        private void generateCards() {
            cards.addAll(Stream.generate(() -> new CardRestDTO(faker.cat().name(), faker.color().name()))
                    .limit(faker.number().numberBetween(3, 8))
                    .collect(Collectors.toSet()));
        }

        public DeckRestDTO build(){
            return new DeckRestDTO(name, info, author, cards);
        }

    }

}
