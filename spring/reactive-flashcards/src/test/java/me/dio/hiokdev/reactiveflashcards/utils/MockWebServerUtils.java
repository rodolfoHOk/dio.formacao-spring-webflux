package me.dio.hiokdev.reactiveflashcards.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.bson.json.JsonObject;
import org.json.JSONArray;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockWebServerUtils {

    public static String getSimpleJson(final String filename) {
        try {
            return new JsonObject(FileUtils.readFileToString(getJsonFile(filename), StandardCharsets.UTF_8)).toString();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String getListJson(final String fileName) {
        try {
            return new JSONArray(FileUtils.readFileToString(getJsonFile(fileName), StandardCharsets.UTF_8)).toString();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static File getJsonFile(String filename) throws FileNotFoundException {
        return ResourceUtils.getFile(String.format("%s%s/%s.json", ResourceUtils.CLASSPATH_URL_PREFIX, "json", filename));
    }

}
