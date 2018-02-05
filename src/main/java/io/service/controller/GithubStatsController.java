package io.service.controller;

import io.service.GithubContributorStats;
import io.service.GithubRepos;
import io.service.GithubTopCommitStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.service.response.JsonResults.ok;

@Singleton
public class GithubStatsController {
    private static final Logger logger = LoggerFactory.getLogger(GithubStatsController.class);
    private GithubRepos githubRepos;
    private GithubContributorStats githubContributorStats;
    private GithubTopCommitStats topCommitStats;

    @Inject
    GithubStatsController(GithubTopCommitStats topCommitStats) {
        this.topCommitStats = topCommitStats;
    }

    public Result topContributors(String organization) {
        return ok(topCommitStats.topContributors(organization));
    }
}