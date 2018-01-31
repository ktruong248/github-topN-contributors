package io.wiser.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import javax.inject.Inject;

/**
 * Ensure play.libs.Json set by the same ObjectMapper that created by {@link GuiceAwareObjectMapperProvider}
 */
class JsonConfigurer {
    @Inject
    JsonConfigurer(ObjectMapper objectMapper) {
        Json.setObjectMapper(objectMapper);
    }
}