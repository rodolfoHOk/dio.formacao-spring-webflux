package me.dio.hiokdev.reactiveflashcards.core.extension.server;

import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@Slf4j
public class WebServerExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private MockWebServer mockWebServer;

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        try {
            log.info("=== Starting mock web server");
            mockWebServer = new MockWebServer();
            mockWebServer.start();
        } catch (Exception exception) {
            log.warn("==== ERROR - Can't start mock web server", exception);
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        try {
            log.info("=== Stopping mock web server");
            mockWebServer.shutdown();
        } catch (Exception exception) {
            log.warn("==== ERROR - Can't finish mock web server", exception);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(WebServer.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (parameterContext.getParameter().isAnnotationPresent(WebServer.class)) {
            return mockWebServer;
        } else {
            return null;
        }
    }

}
