package me.dio.hiokdev.reactiveflashcards.utils.request;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactiveflashcards.utils.asserts.EmptyBodyAssertUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class NoBodyRequestBuilder {

    private final WebTestClient webTestClient;
    private final Function<UriBuilder, URI> uriFunction;
    private final Map<String, Set<String>> headers;
    private final Object body;

    public EmptyBodyAssertUtils doPost() {
        var preResponse = webTestClient.post().uri(uriFunction).accept(MediaType.APPLICATION_JSON);
        if (!headers.isEmpty()) {
            headers.forEach((k,v) -> preResponse.header(k, v.toArray(String[]::new)));
        }
        if (Objects.nonNull(body)) {
            return new EmptyBodyAssertUtils(preResponse
                    .bodyValue(body).exchange().expectBody().isEmpty());
        }
        return new EmptyBodyAssertUtils(preResponse.exchange().expectBody().isEmpty());
    }

    public EmptyBodyAssertUtils doPut() {
        var preResponse = webTestClient.put().uri(uriFunction).accept(MediaType.APPLICATION_JSON);
        if (!headers.isEmpty()) {
            headers.forEach((k,v) -> preResponse.header(k, v.toArray(String[]::new)));
        }
        if (Objects.nonNull(body)) {
            return new EmptyBodyAssertUtils(preResponse
                    .bodyValue(body).exchange().expectBody().isEmpty());
        }
        return new EmptyBodyAssertUtils(preResponse.exchange().expectBody().isEmpty());
    }

    public EmptyBodyAssertUtils doDelete() {
        var preResponse = webTestClient.delete().uri(uriFunction).accept(MediaType.APPLICATION_JSON);
        if (!headers.isEmpty()) {
            headers.forEach((k,v) -> preResponse.header(k, v.toArray(String[]::new)));
        }
        return new EmptyBodyAssertUtils(preResponse.exchange().expectBody().isEmpty());
    }

}
