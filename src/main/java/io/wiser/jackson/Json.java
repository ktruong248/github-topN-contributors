package io.wiser.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

public class Json {
    private final ObjectMapper objectMapper;

    @Inject
    Json(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convert a JsonNode to a Java value.
     *
     * @param json  json string to convert.
     * @param clazz Expected Java value type.
     */
    public <A> A parse(String json, Class<A> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <A> A parse(byte[] source, Class<A> clazz) {
        try {
            return objectMapper.readValue(source, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <A> A parse(JsonNode json, Class<A> clazz) {
        try {
            return objectMapper.readerFor(clazz).readValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <A> A parse(JsonNode json, TypeReference<A> type) {
        try {
            return objectMapper.readerFor(type).readValue(json);
        } catch (IOException e) {
            ///CLOVER:OFF
            throw new RuntimeException(e);
            ///CLOVER:ON
        }
    }

    public JsonNode parse(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <A> A parse(@Nullable JsonNode node, Class<A> clazz, @Nullable A defaultValue) {
        return (node != null) ? parse(node, clazz) : defaultValue;
    }

    JsonNode valueToTree(Object fromValue) {
        return objectMapper.valueToTree(fromValue);
    }

    public String pretty(String json) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parse(json));
        } catch (JsonProcessingException e) {
            ///CLOVER:OFF
            throw new RuntimeException(e);
            ///CLOVER:ON
        }
    }

    String pretty(Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            ///CLOVER:OFF
            throw new RuntimeException(e);
            ///CLOVER:ON
        }
    }

    public String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            ///CLOVER:OFF
            throw new RuntimeException(e);
            ///CLOVER:ON
        }
    }

    public Set<String> parseJsonStringAsSet(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Set<String>>() {});
        } catch (IOException e) {
            ///CLOVER:OFF
            throw new RuntimeException(e);
            ///CLOVER:ON
        }
    }

    /**
     * Convert an object to JsonNode.
     *
     * @param data Value to convert in Json.
     */
    public JsonNode toJson(final Object data) {
        return objectMapper.valueToTree(data);
    }

    public int getInt(JsonNode node, String propertyName, int defaultValue) {
        Preconditions.checkNotNull(node, "node can not be null");
        JsonNode nodeValue = node.get(propertyName);
        return (nodeValue != null && nodeValue.isInt()) ? nodeValue.intValue() : defaultValue;
    }

    public boolean isMissingOrNull(@Nullable JsonNode node) {
        return node == null || node.isNull();
    }
}