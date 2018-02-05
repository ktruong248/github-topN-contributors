package io.service.response;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Result;

import static io.jackson.JsonHelper.toJson;
import static play.mvc.Http.Status.*;
import static play.mvc.Results.status;

public class JsonResults {
    public static Result ok(Object content) {
        return status(OK, toJson(content));
    }

    public static Result ok(JsonNode content) {
        return status(OK, content);
    }
}
