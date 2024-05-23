package me.dio.hiokdev.reactiveflashcards.utils.request;

import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class RequestBuilder<T> {

    private final WebTestClient webTestClient;
    private Function<UriBuilder, URI> uriFunction;
    private final Map<String, Set<String>> headers = new HashMap<>();
    private Object body;
    private final Class<T> responseClass;

    public RequestBuilder(
            final ApplicationContext applicationContext,
            final String baseUrl,
            final Class<T> responseClass
    ) {
        this.responseClass = responseClass;
        this.webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl(baseUrl)
                .responseTimeout(Duration.ofDays(1))
                .build();
    }

    public static RequestBuilder<Void> noContentRequestBuilder(
            final ApplicationContext applicationContext,
            final String baseUrl
    ) {
        return new RequestBuilder<>(applicationContext, baseUrl, Void.class);
    }

    public static RequestBuilder<ProblemResponse> problemResponseRequestBuilder(
            final ApplicationContext applicationContext,
            final String baseUrl
    ) {
        return new RequestBuilder<>(applicationContext, baseUrl, ProblemResponse.class);
    }

    public RequestBuilder<T> uri(final Function<UriBuilder, URI> uriFunction) {
        this.uriFunction = uriFunction;
        return this;
    }

    public RequestBuilder<T> body(final Object body) {
        this.body = body;
        return this;
    }

    public RequestBuilder<T> header(final String key, final Set<String> value) {
        this.headers.put(key, value);
        return this;
    }

    public SimpleRequestBuilder<T> generateRequestWithSimpleBody() {
        if (Objects.isNull(uriFunction)) {
            throw new IllegalArgumentException("Informe a URI do recurso a ser consumido");
        }
        return new SimpleRequestBuilder<T>(webTestClient, uriFunction, headers, body, responseClass);
    }

    public NoBodyRequestBuilder generateRequestWithoutBody() {
        if (Objects.isNull(uriFunction)) {
            throw new IllegalArgumentException("Informe a URI do recurso a ser consumido");
        }
        if (responseClass != Void.class) {
            throw new IllegalArgumentException("Use a classe Void para requisições sem response body");
        }
        return new NoBodyRequestBuilder(webTestClient, uriFunction, headers, body);
    }

    public CollectionRequestBuilder<T> generateRequestWithCollectionBody() {
        if (Objects.isNull(uriFunction)) {
            throw new IllegalArgumentException("Informe a URI do recurso a ser consumido");
        }
        if (responseClass == Void.class) {
            throw new IllegalArgumentException("Não use a classe Void para requisições com response body de coleções");
        }
        return new CollectionRequestBuilder<T>(webTestClient, uriFunction, headers, body, responseClass);
    }

}
