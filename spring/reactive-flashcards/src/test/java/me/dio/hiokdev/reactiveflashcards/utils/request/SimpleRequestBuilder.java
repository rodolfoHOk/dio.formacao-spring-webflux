package me.dio.hiokdev.reactiveflashcards.utils.request;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.utils.asserts.SimpleBodyAssertUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class SimpleRequestBuilder<T> {

    private final WebTestClient webTestClient;
    private final Function<UriBuilder, URI> uriFunction;
    private final Map<String, Set<String>> headers;
    private final Object body;
    private final Class<T> responseClass;

    public SimpleBodyAssertUtils<T> doPost() {
        var preResponse = webTestClient.post().uri(uriFunction).accept(MediaType.APPLICATION_JSON);
        if (!headers.isEmpty()) {
            headers.forEach((k,v) -> preResponse.header(k, v.toArray(String[]::new)));
        }
        if (Objects.nonNull(body)) {
            return new SimpleBodyAssertUtils<>(preResponse
                    .bodyValue(body).exchange().expectBody(responseClass).returnResult());
        }
        return new SimpleBodyAssertUtils<>(preResponse.exchange().expectBody(responseClass).returnResult());
    }

    public SimpleBodyAssertUtils<T> doGet() {
        var preResponse = webTestClient.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON);
        if (!headers.isEmpty()) {
            headers.forEach((k,v) -> preResponse.header(k, v.toArray(String[]::new)));
        }
        return new SimpleBodyAssertUtils<>(preResponse.exchange().expectBody(responseClass).returnResult());
    }

    public SimpleBodyAssertUtils<T> doPut() {
        var preResponse = webTestClient.put().uri(uriFunction).accept(MediaType.APPLICATION_JSON);
        if (!headers.isEmpty()) {
            headers.forEach((k,v) -> preResponse.header(k, v.toArray(String[]::new)));
        }
        if (Objects.nonNull(body)) {
            return new SimpleBodyAssertUtils<>(preResponse
                    .bodyValue(body).exchange().expectBody(responseClass).returnResult());
        }
        return new SimpleBodyAssertUtils<>(preResponse.exchange().expectBody(responseClass).returnResult());
    }

    public SimpleBodyAssertUtils<T> doDelete() {
        var preResponse = webTestClient.delete().uri(uriFunction).accept(MediaType.APPLICATION_JSON);
        if (!headers.isEmpty()) {
            headers.forEach((k,v) -> preResponse.header(k, v.toArray(String[]::new)));
        }
        return new SimpleBodyAssertUtils<>(preResponse.exchange().expectBody(responseClass).returnResult());
    }

}
