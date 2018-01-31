package io.wiser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CachedGithubReposTest {
    @Mock private WSClient wsClient;
    @Mock private WSRequest urlRequest;
    @Mock private CompletionStage<WSResponse> wsResponseCompletionStage;
    @Mock private CompletableFuture<WSResponse> completableFuture;
    @Mock private WSResponse wsResponse;
    private ArrayNode arrayJsonResponse;
    private ObjectNode rateLimitExceedJsonResponse;

    private Cache<String, List<String>> cache;
    private String organization;
    private CachedGithubRepos cachedGithubRepos;

    @Before
    public void setUp() throws Exception {
        organization = "Github";
        cache = Caffeine.newBuilder().build();
        cachedGithubRepos = new CachedGithubRepos(wsClient, cache);
        arrayJsonResponse = JsonNodeFactory.instance.arrayNode();

        ObjectNode repoJson = JsonNodeFactory.instance.objectNode();
        repoJson.put("name", "repo1");
        arrayJsonResponse.add(repoJson);

        rateLimitExceedJsonResponse = JsonNodeFactory.instance.objectNode();
        rateLimitExceedJsonResponse.put("message", "rate limit exceed ....");
    }

    @Test
    public void shouldReturnRepoNamesAndCachedResult() throws ExecutionException, InterruptedException {
        when(wsClient.url("https://api.github.com/orgs/Github/repos")).thenReturn(urlRequest);
        when(urlRequest.setRequestTimeout(5000)).thenReturn(urlRequest);
        when(urlRequest.get()).thenReturn(wsResponseCompletionStage);
        when(wsResponseCompletionStage.toCompletableFuture()).thenReturn(completableFuture);
        when(completableFuture.get()).thenReturn(wsResponse);
        when(wsResponse.asJson()).thenReturn(arrayJsonResponse);

        List<String> repoNames = cachedGithubRepos.getRepositories(organization);
        assertThat(repoNames.size()).isEqualTo(arrayJsonResponse.size());
        assertThat(cache.getIfPresent(organization.toLowerCase())).containsExactly("repo1");
    }

    @Test
    public void shouldHandleRateLimitExceeds() throws ExecutionException, InterruptedException {
        when(wsClient.url("https://api.github.com/orgs/Github/repos")).thenReturn(urlRequest);
        when(urlRequest.setRequestTimeout(5000)).thenReturn(urlRequest);
        when(urlRequest.get()).thenReturn(wsResponseCompletionStage);
        when(wsResponseCompletionStage.toCompletableFuture()).thenReturn(completableFuture);
        when(completableFuture.get()).thenReturn(wsResponse);
        when(wsResponse.asJson()).thenReturn(rateLimitExceedJsonResponse);

        List<String> repoNames = cachedGithubRepos.getRepositories(organization);
        assertThat(repoNames.size()).isEqualTo(0);
        assertThat(cache.getIfPresent(organization.toLowerCase())).isEmpty();
    }
}