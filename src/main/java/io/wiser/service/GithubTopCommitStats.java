package io.wiser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.benmanes.caffeine.cache.Cache;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GithubTopCommitStats {
    private final GithubRepos githubRepos;
    private final GithubContributorStats githubContributorStats;
    private final Cache<String, List<ContributorStats>> cache;
    private Comparator<? super ContributorStats> contributorStatSort;

    @Inject
    public GithubTopCommitStats(GithubRepos githubRepos,
                                GithubContributorStats githubContributorStats,
                                @Named("TopCommitStatsCache") Cache<String, List<ContributorStats>> cache,
                                @Named("CommitSortDescending") Comparator<ContributorStats> sort) {
        this.githubRepos = githubRepos;
        this.githubContributorStats = githubContributorStats;
        this.cache = cache;
        this.contributorStatSort = sort;
    }

    public List<ContributorStats> topContributors(String organization) {
        return cache.get(organization.trim().toLowerCase(), s -> {
            List<ContributorStats> stats = new LinkedList<>();
            List<String> repositories = githubRepos.getRepositories(organization);
            repositories.forEach(repoName -> {
                ArrayNode contributorStats = githubContributorStats.getContributorStats(organization, repoName);
                for (int index = 0; index < contributorStats.size(); index++) {
                    JsonNode statsJson = contributorStats.get(index);
                    int totalWeeklyCommits = statsJson.get("total").asInt();
                    String login = statsJson.get("author").get("login").asText();
                    stats.add(new ContributorStats(repoName, login, totalWeeklyCommits));
                }
            });

            stats.sort(contributorStatSort);
            return stats;
        });
    }
}
