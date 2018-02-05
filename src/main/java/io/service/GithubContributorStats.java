package io.service;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface GithubContributorStats {
    ArrayNode getContributorStats(String organization, String repoName);
}
