package io.service;

import com.fasterxml.jackson.databind.JsonNode;
//import com.google.common.cache.Cache;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedList;
import java.util.List;

public class CachedGithubRepos implements GithubRepos {
    private static final Logger logger = LoggerFactory.getLogger(CachedGithubRepos.class);
    private final Cache<String, List<String>> repositoriesCache;
    private final WSClient wsClient;
    private long timeoutMs;

    @Inject
    CachedGithubRepos(WSClient wsClient,
                      @Named("GithubReposCache") Cache<String, List<String>> repositoriesCache) {
        this.wsClient = wsClient;
        this.repositoriesCache = repositoriesCache;
        this.timeoutMs = 5000;
    }

    public List<String> getRepositories(String organization) {
        return repositoriesCache.get(organization.toLowerCase(), org -> {
            String url = String.format("https://api.github.com/orgs/%1s/repos", organization);
            JsonNode response = WsClientUtils.makeRequest(wsClient, url, timeoutMs);
            logger.debug("organization={} response={}", organization, response);
            if (isRateLimitExceeded(response) || !(response instanceof ArrayNode)) {
                logger.warn("received rate limit exceed");
                // store empty so that we can return as if empty
                return ImmutableList.of();
            }

            List<String> repositoriesName = new LinkedList<>();
            ArrayNode gitRepositories = (ArrayNode) response;
            for (int index = 0; index < gitRepositories.size(); index++) {
                repositoriesName.add(gitRepositories.get(index).get("name").asText());
            }

            logger.debug("organization={} and repositoriesName={}", organization, repositoriesName);
            return repositoriesName;
        });
    }

    // can be improve with better checking
    private boolean isRateLimitExceeded(JsonNode response) {
        return (response.has("message") && response.get("message").asText().contains("rate limit"));
    }
}
