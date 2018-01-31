package io.wiser.service;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

final class WsClientUtils {
    private WsClientUtils() {}

    static JsonNode makeRequest(WSClient wsClient, String url, long timeoutMs) {
        try {
            return wsClient.url(url)
                    .setRequestTimeout(timeoutMs)
                    .get()
                    .toCompletableFuture()
                    .get().asJson();
        } catch (Exception e) {
            // to can be improve with better runtime exception
            throw new RuntimeException(String.format("Failed to fetch data from url %1s", url), e);
        }
    }
}
