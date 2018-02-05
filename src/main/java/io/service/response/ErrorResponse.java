package io.service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ErrorResponse {
    private static final Logger logger = LoggerFactory.getLogger(ErrorResponse.class);

    @JsonProperty
    private ObjectNode error;

    public ErrorResponse(String message) {
        error = Json.newObject().put("message", message);
    }

    public ErrorResponse(Throwable throwable) {
        this(rootMessage(throwable));
        error.set("details", buildStackTrace(throwable));
    }

    private static String rootMessage(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }

        return String.format("%s: %s", throwable.getClass().getCanonicalName(), throwable.getMessage());
    }

    private static ArrayNode buildStackTrace(Throwable throwable) {
        ArrayNode asJson = Json.newArray();
        try {
            String label = "Error";
            while (throwable != null) {
                ObjectNode jsonThrowable = asJson.addObject();
                String summary = String.format("%1$s: %2$s",
                        throwable.getClass().getCanonicalName(),
                        throwable.getMessage());
                jsonThrowable.put(label, summary);
                ArrayNode trace = jsonThrowable.putArray("trace");
                for (StackTraceElement element : throwable.getStackTrace()) {
                    trace.add(String.format("at %s.%s(%s:%s)",
                            element.getClassName(),
                            element.getMethodName(),
                            element.getFileName(),
                            element.getLineNumber()));
                }

                label = "Caused by";
                throwable = throwable.getCause();
            }

            return asJson;
        } catch (Exception e) {
            logger.error("Unable to build stack trace", e);
            return asJson;
        }
    }
}