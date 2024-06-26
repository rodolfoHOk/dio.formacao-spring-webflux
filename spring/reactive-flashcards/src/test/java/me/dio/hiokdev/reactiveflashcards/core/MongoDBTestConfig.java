package me.dio.hiokdev.reactiveflashcards.core;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import me.dio.hiokdev.reactiveflashcards.core.mongo.converter.DateToOffsetDateTimeConverter;
import me.dio.hiokdev.reactiveflashcards.core.mongo.converter.OffsetDateTimeToDateConverter;
import me.dio.hiokdev.reactiveflashcards.core.mongo.provider.OffsetDateTimeProvider;
import me.dio.hiokdev.reactiveflashcards.domain.repository.DeckRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@TestConfiguration
@EnableReactiveMongoRepositories(basePackageClasses = DeckRepository.class)
public class MongoDBTestConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @NotNull
    @Override
    protected String getDatabaseName() {
        return "reactive-flashcards-test";
    }

    @NotNull
    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(uri);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

    @NotNull
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new DateToOffsetDateTimeConverter());
        converters.add(new OffsetDateTimeToDateConverter());
        return new MongoCustomConversions(converters);
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new OffsetDateTimeProvider();
    }

}
