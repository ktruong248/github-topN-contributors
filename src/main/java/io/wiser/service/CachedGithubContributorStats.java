package io.wiser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import javax.inject.Named;

import static io.wiser.service.WsClientUtils.makeRequest;

public class CachedGithubContributorStats implements GithubContributorStats {
    private static final Logger logger = LoggerFactory.getLogger(CachedGithubContributorStats.class);
    private final Cache<Key, ArrayNode> repositoriesCache;
    private final WSClient wsClient;
    private final long timeoutMs;

    @Inject
    public CachedGithubContributorStats(WSClient wsClient,
                                        @Named("GithubContributorStatsCache") Cache<Key, ArrayNode> repositoriesCache) {
        this.wsClient = wsClient;
        this.repositoriesCache = repositoriesCache;
        this.timeoutMs = 5000;
    }

    /**
     * return cached top contributor for the given organization. If the data expired or not yet in the cache,
     * it'll fetch data from from github.
     *
     * @param organization the github organization name
     * @param repo         the github repo name under the organization
     * @return the array json node
     * @see <a href="https://developer.github.com/v3/repos/statistics/#get-contributors-list-with-additions-deletions-and-commit-counts">Github api</a>
     **/
    @Override
    public ArrayNode getContributorStats(String organization, String repo) {
        // GET /repos/:owner/:repo/stats/contributors
        return repositoriesCache.get(buildKey(organization, repo), key -> {
            String url = String.format("https://api.github.com/repos/%1s/%2s/stats/contributors", organization, repo);
            JsonNode responseJson = makeRequest(wsClient, url, timeoutMs);
            logger.debug("fetched url={} response={}", url, responseJson);
            if (responseJson instanceof ArrayNode) {
                return (ArrayNode) responseJson;
            }

            // look like github return {} intermittently so assume this is because of rate limit
            return JsonNodeFactory.instance.arrayNode();
        });
    }

    private Key buildKey(String org, String repoName) {
        return new Key(org.trim().toLowerCase(), repoName.trim().toLowerCase());
    }

    public static final class Key {
        private final String organization;
        private final String repoName;

        Key(String organization, String repoName) {
            this.organization = organization;
            this.repoName = repoName;
        }

        public String getOrganization() {
            return organization;
        }

        public String getRepoName() {
            return repoName;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof Key)) {
                return false;
            }

            Key key = (Key) other;
            return Objects.equal(organization, key.organization) &&
                    Objects.equal(repoName, key.repoName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(organization, repoName);
        }
    }
}
