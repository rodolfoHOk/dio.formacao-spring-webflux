package me.dio.hiokdev.reactiveflashcards.core.mongo;

import me.dio.hiokdev.reactiveflashcards.core.mongo.converter.DateToOffsetDateTimeConverter;
import me.dio.hiokdev.reactiveflashcards.core.mongo.converter.OffsetDateTimeToDateConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?,?>> converters = List.of(
                new OffsetDateTimeToDateConverter(),
                new DateToOffsetDateTimeConverter()
        );
        return new MongoCustomConversions(converters);
    }

}
