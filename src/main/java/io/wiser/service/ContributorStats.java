package io.wiser.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class ContributorStats {
    @JsonProperty("repo")
    private String repoName;
    private String login;
    @JsonProperty("total")
    private int totalCommits;

    public ContributorStats(String repoName, String login, int totalCommits) {
        this.repoName = repoName;
        this.login = login;
        this.totalCommits = totalCommits;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getLogin() {
        return login;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("repoName", repoName)
                .add("login", login)
                .add("totalCommits", totalCommits)
                .toString();
    }
}
