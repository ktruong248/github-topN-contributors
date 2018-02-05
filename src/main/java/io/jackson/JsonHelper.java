package io.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.annotation.Nullable;
import java.util.Set;

public class JsonHelper {
    private static final Json json = new Json(buildObjectMapper());

    public static ObjectMapper buildObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new GuavaModule());
    }

    public static Json instance() {
        return json;
    }

    public static <A> A parse(String json, Class<A> clazz) {
        return instance().parse(json, clazz);
    }

    public static <A> A parse(byte[] source, Class<A> clazz) {
        return instance().parse(source, clazz);
    }

    public static <A> A parse(JsonNode json, Class<A> clazz) {
        return instance().parse(json, clazz);
    }

    public static <A> A parse(@Nullable JsonNode json, Class<A> clazz, @Nullable A defaultValue) {
        return instance().parse(json, clazz, defaultValue);
    }

    public static <A> A parse(JsonNode json, TypeReference<A> type) {
        return instance().parse(json, type);
    }

    public static JsonNode parse(String json) {
        return instance().parse(json);
    }

    public static JsonNode valueToTree(Object fromValue) {
        return instance().valueToTree(fromValue);
    }

    public static String writeValueAsString(Object value) {
        return instance().writeValueAsString(value);
    }

    public static Set<String> parseJsonStringAsSet(String json) {
        return instance().parseJsonStringAsSet(json);
    }

    public static JsonNode toJson(final Object data) {
        return instance().toJson(data);
    }

    public static int getInt(JsonNode node, String propertyName, int defaultValue) {
        return instance().getInt(node, propertyName, defaultValue);
    }
}