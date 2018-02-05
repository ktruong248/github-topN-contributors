package io.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.service.CachedGithubContributorStats.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CachedGithubContributorStatsTest {
    @Mock
    private WSClient wsClient;
    @Mock
    private WSRequest urlRequest;
    @Mock
    private CompletionStage<WSResponse> wsResponseCompletionStage;
    @Mock
    private CompletableFuture<WSResponse> completableFuture;
    @Mock
    private WSResponse wsResponse;
    @Mock
    ArrayNode arrayJsonResponse;
    @Mock
    JsonNode jsonNode;

    private Cache<Key, ArrayNode> cache;
    private CachedGithubContributorStats contributorStats;
    private String organization;

    @Before
    public void setup() {
        organization = "Github";
        cache = Caffeine.newBuilder().build();
        contributorStats = new CachedGithubContributorStats(wsClient, cache);
    }

    @Test
    public void shouldFetchDataFromGitHubAndCachedResult() throws ExecutionException, InterruptedException {
        when(wsClient.url("https://api.github.com/repos/Github/repoName/stats/contributors")).thenReturn(urlRequest);
        when(urlRequest.setRequestTimeout(5000)).thenReturn(urlRequest);
        when(urlRequest.get()).thenReturn(wsResponseCompletionStage);
        when(wsResponseCompletionStage.toCompletableFuture()).thenReturn(completableFuture);
        when(completableFuture.get()).thenReturn(wsResponse);
        when(wsResponse.asJson()).thenReturn(arrayJsonResponse);

        String repoName = "repoName";
        ArrayNode contributors = contributorStats.getContributorStats(organization, repoName);
        assertThat(contributors).isSameAs(arrayJsonResponse);
        assertThat(cache.getIfPresent(new Key(organization.toLowerCase(), repoName.toLowerCase())))
                .isSameAs(arrayJsonResponse);
    }

    @Test
    public void shouldNotFetchDataFromGitHubAndIfDataCached() {
        String repoName = "repoName";
        cache.put(new Key(organization.toLowerCase(), repoName.toLowerCase()), arrayJsonResponse);

        ArrayNode contributors = contributorStats.getContributorStats(organization, repoName);
        assertThat(contributors).isSameAs(arrayJsonResponse);
        verifyZeroInteractions(wsClient);
    }

    @Test
    public void shouldReturnEmptyArrayNodeWhenGithubReturnEmptyJsonNode() throws ExecutionException,
            InterruptedException {
        String repoName = "repoName";
        when(wsClient.url("https://api.github.com/repos/Github/repoName/stats/contributors")).thenReturn(urlRequest);
        when(urlRequest.setRequestTimeout(5000)).thenReturn(urlRequest);
        when(urlRequest.get()).thenReturn(wsResponseCompletionStage);
        when(wsResponseCompletionStage.toCompletableFuture()).thenReturn(completableFuture);
        when(completableFuture.get()).thenReturn(wsResponse);
        when(wsResponse.asJson()).thenReturn(jsonNode);

        ArrayNode contributors = contributorStats.getContributorStats(organization, repoName);
        assertThat(contributors.size()).isEqualTo(0);
    }
}