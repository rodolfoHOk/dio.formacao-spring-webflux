package me.dio.hiokdev.reactiveflashcards.core.factorybot;

import com.github.javafaker.Faker;
import lombok.Getter;

import java.util.Locale;

public class RandomData {

    @Getter
    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

}
