package me.dio.hiokdev.reactiveflashcards.api.controller;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactiveflashcards.ReactiveFlashcardsApplication;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.ApiExceptionHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ConstraintViolationHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.DeckInStudyHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.EmailAlreadyUsedHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.GenericExceptionHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.JsonProcessingHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.MethodNotAllowedHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.NotFoundHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ReactiveFlashCardsHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ResponseStatusHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.WebExchangeBindHandler;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.mongo.provider.OffsetDateTimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@ContextConfiguration(classes = {
        OffsetDateTimeProvider.class, ApiExceptionHandler.class, DeckInStudyHandler.class,
        EmailAlreadyUsedHandler.class, MethodNotAllowedHandler.class, NotFoundHandler.class,
        ConstraintViolationHandler.class, WebExchangeBindHandler.class, ResponseStatusHandler.class,
        ReactiveFlashCardsHandler.class, GenericExceptionHandler.class, JsonProcessingHandler.class,
        ReactiveFlashcardsApplication.class, MongoMappingContext.class
})
public abstract class AbstractControllerTest {

    @Autowired
    protected ApplicationContext applicationContext;

    protected final Faker faker = RandomData.getFaker();

}
