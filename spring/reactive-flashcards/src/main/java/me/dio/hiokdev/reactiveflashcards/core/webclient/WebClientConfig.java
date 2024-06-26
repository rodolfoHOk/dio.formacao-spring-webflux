package me.dio.hiokdev.reactiveflashcards.core.webclient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public HttpClient httpClient(
            @Value("${reactive-flashcards.http-client.response-timeout}") final Long responseTimeout,
            @Value("${reactive-flashcards.http-client.read-timeout}") final Long readTimeout
    ) {
        return HttpClient.create()
                .responseTimeout(Duration.ofMillis(responseTimeout))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));
    }

    @Bean
    public ClientHttpConnector clientHttpConnector(final HttpClient httpClient) {
        return new ReactorClientHttpConnector(httpClient);
    }

    @Bean
    public WebClient webClient(final WebClient.Builder builder, final ClientHttpConnector connector) {
        log.info("==== Creating a WebClient");
        return builder.clientConnector(connector).build();
    }

}
