package io.wiser.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.wiser.service.sort.CommitTotalDescending;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GithubTopCommitStatsTest {

    @Mock private GithubContributorStats githubContributorStats;
    @Mock private GithubRepos githubRepos;
    @Mock private ArrayNode repo1Contributor;
    @Mock private ArrayNode repo2Contributor;
    private Cache<String, List<ContributorStats>> cache;
    private String organization;
    private GithubTopCommitStats stats;

    @Before
    public void setup() {
        Comparator<ContributorStats> sort = new CommitTotalDescending();
        cache = Caffeine.newBuilder().build();
        organization = "Github";
        stats = new GithubTopCommitStats(githubRepos, githubContributorStats, cache, sort);

        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        repo1Contributor = jsonNodeFactory.arrayNode();
        repo1Contributor.add(sampleContributor("login1", 1));
        repo1Contributor.add(sampleContributor("login11", 10));

        repo2Contributor = jsonNodeFactory.arrayNode();
        repo2Contributor.add(sampleContributor("login2", 100));
    }

    @Test
    public void shouldCallReposSortAndCacheResult() {
        when(githubRepos.getRepositories(organization)).thenReturn(asList("repo1", "repo2"));
        when(githubContributorStats.getContributorStats(organization, "repo1")).thenReturn(repo1Contributor);
        when(githubContributorStats.getContributorStats(organization, "repo2")).thenReturn(repo2Contributor);

        List<ContributorStats> results = stats.topContributors(organization);
        assertThat(results.size()).isEqualTo(3);
        List<ContributorStats> cachedResults = cache.getIfPresent(organization.toLowerCase());
        assertThat(cachedResults).isNotNull();
        assertThat(cachedResults.size()).isEqualTo(3);
        assertThat(results.get(0)).isEqualTo(cachedResults.get(0));
        assertThat(results.get(1)).isEqualTo(cachedResults.get(1));
        assertThat(results.get(2)).isEqualTo(cachedResults.get(2));
    }

    @Test
    public void shouldNotCallReposIfTheDataIsCached() {
        cache.put(organization.toLowerCase(),
                singletonList(new ContributorStats(organization, "login1", 20)));

        stats.topContributors(organization);
        verifyZeroInteractions(githubRepos, githubContributorStats);
    }

    private ObjectNode sampleContributor(String loginId, int totalCommits) {
        JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

        ObjectNode contributor = jsonNodeFactory.objectNode();
        contributor.put("total", totalCommits);
        ObjectNode author1 = jsonNodeFactory.objectNode();
        author1.put("login", loginId);
        contributor.set("author", author1);

        return contributor;
    }
}