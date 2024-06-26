package me.dio.hiokdev.reactiveflashcards.core.factorybot;

import com.github.javafaker.Faker;
import lombok.Getter;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RandomData {

    @Getter
    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    public static <T extends Enum<?>> T randomEnum(final Class<T> clazz) {
        var x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static <T extends Enum<?>> T randomEnum(final Class<T> clazz, final List<Integer> blackList) {
        var x = 0;
        do {
            x = new Random().nextInt(clazz.getEnumConstants().length);
        } while (blackList.contains(x));
        return clazz.getEnumConstants()[x];
    }

}
