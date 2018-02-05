package io.service;

import java.util.List;

public interface GithubRepos {
    List<String> getRepositories(String organization);
}
