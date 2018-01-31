package io.wiser.service.controller;

import io.wiser.service.GithubContributorStats;
import io.wiser.service.GithubRepos;
import io.wiser.service.GithubTopCommitStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.wiser.service.response.JsonResults.ok;

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