package me.dio.hiokdev.reactiveflashcards.domain.exception;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class BaseErrorMessage {

    private static final String DEFAULT_RESOURCE = "messages";

    private static final BaseErrorMessage GENERIC_EXCEPTIONS = new BaseErrorMessage("generic");
    private static final BaseErrorMessage GENERIC_NOT_FOUND = new BaseErrorMessage("generic.notFound");
    private static final BaseErrorMessage GENERIC_METHOD_NOT_ALLOWED = new BaseErrorMessage("generic.methodNotAllowed");

    private final String key;
    private String[] params;

    public BaseErrorMessage params(final String... params) {
        this.params = ArrayUtils.clone(params);
        return this;
    }

    public String getMessage() {
        var message = tryGetMessageFromBundle();
        if (ArrayUtils.isNotEmpty(params)) {
            var fmt = new MessageFormat(message);
            message = fmt.format(params);
        }
        return message;
    }

    private String tryGetMessageFromBundle() {
        return getResource().getString(key);
    }

    private ResourceBundle getResource() {
        return ResourceBundle.getBundle(DEFAULT_RESOURCE);
    }

}
