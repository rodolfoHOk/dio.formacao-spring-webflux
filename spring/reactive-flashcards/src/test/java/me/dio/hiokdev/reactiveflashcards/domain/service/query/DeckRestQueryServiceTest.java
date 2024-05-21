package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import me.dio.hiokdev.reactiveflashcards.core.extension.server.WebServer;
import me.dio.hiokdev.reactiveflashcards.core.extension.server.WebServerExtension;
import me.dio.hiokdev.reactiveflashcards.core.webclient.DeckApiConfig;
import me.dio.hiokdev.reactiveflashcards.utils.MockWebServerUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(WebServerExtension.class)
public class DeckRestQueryServiceTest {

    private final WebClient webClient = WebClient.builder().build();
    private DeckRestQueryService deckRestQueryService;
    private MockWebServer mockWebServer;

    @BeforeEach
    void setup(@WebServer final MockWebServer mockWebServer) {
        this.mockWebServer = mockWebServer;
        var deckApiConfig = new DeckApiConfig(mockWebServer.url("/").toString(), "/auth", "/decks");
        deckRestQueryService = new DeckRestQueryService(webClient, deckApiConfig);
    }

    @Test
    void getDecksTest() {
        var authResponse = new MockResponse();
        authResponse.setResponseCode(HttpStatus.OK.value());
        authResponse.setBody(MockWebServerUtils.getSimpleJson("authResponse"));
        authResponse.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        var decksResponse = new MockResponse();
        decksResponse.setResponseCode(HttpStatus.OK.value());
        decksResponse.setBody(MockWebServerUtils.getListJson("decksResponse"));
        decksResponse.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        var decksCacheResponse = new MockResponse();
        decksCacheResponse.setResponseCode(HttpStatus.OK.value());
        decksCacheResponse.setBody(MockWebServerUtils.getListJson("decksResponse"));
        decksCacheResponse.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        mockWebServer.enqueue(authResponse);
        mockWebServer.enqueue(decksResponse);
        mockWebServer.enqueue(decksCacheResponse);

        StepVerifier.create(deckRestQueryService.getDecks())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> actual.forEach(a -> assertThat(a).hasNoNullFieldsOrProperties()))
                .verifyComplete();

        StepVerifier.create(deckRestQueryService.getDecks())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> actual.forEach(a -> assertThat(a).hasNoNullFieldsOrProperties()))
                .verifyComplete();
    }

}
