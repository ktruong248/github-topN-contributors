package io.wiser.guice;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import io.wiser.common.Sleeper;
import io.wiser.common.SystemSleeper;
import io.wiser.service.*;
import io.wiser.service.sort.CommitTotalDescending;
import io.wiser.shutdown.ApplicationShutdownHandler;
import io.wiser.shutdown.EnterShuttingDownStatusHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.Environment;
import play.mvc.Controller;
import play.mvc.Http;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;

public class ServiceModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(ServiceModule.class);
    private final Configuration configuration;

    public ServiceModule(@SuppressWarnings("unused") Environment environment, Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bindConfiguration(configuration);
        bind(Clock.class).toInstance(Clock.systemUTC());
        bind(Sleeper.class).to(SystemSleeper.class);
        bind(GithubRepos.class).to(CachedGithubRepos.class);
        bind(GithubContributorStats.class).to(CachedGithubContributorStats.class);
        bind(new TypeLiteral<Comparator<ContributorStats>>(){})
                .annotatedWith(Names.named("CommitSortDescending"))
                .toInstance(new CommitTotalDescending());
        bind(new TypeLiteral<Supplier<Http.Request>>() {}).toInstance(Controller::request);
        bind(AtomicBoolean.class).annotatedWith(Names.named("ShuttingDownStatus")).toInstance(new AtomicBoolean(false));
    }

    @Provides
    @Singleton
    @Named("TopCommitStatsCache")
    Cache<String, List<ContributorStats>> providesGithubTopCommitStatsCache() {
        // hard code right now it can be easily in configuration
        return Caffeine.newBuilder()
                .expireAfterWrite(120, TimeUnit.MINUTES)
                .maximumSize(200)
                .build();
    }

    @Provides
    @Singleton
    @Named("GithubReposCache")
    Cache<String, List<String>> providesGithubReposCache() {
        // hard code right now it can be easily in configuration
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(200)
                .build();
    }

    @Provides
    @Singleton
    @Named("GithubContributorStatsCache")
    Cache<CachedGithubContributorStats.Key, ArrayNode> providesGithubContributorStatsCache() {
        // hard code right now it can be easily in configuration
        return Caffeine.newBuilder()
                .expireAfterWrite(120, TimeUnit.MINUTES)
                .maximumSize(200)
                .build();
    }

    @Provides
    @Singleton
    ApplicationShutdownHandler provideApplicationShutdownHandler(
            EnterShuttingDownStatusHook enterShuttingDownStatusHook) {
        return new ApplicationShutdownHandler(ImmutableList.of(
                enterShuttingDownStatusHook
        ));
    }

    // We bind Play application configuration constants as constants within Guice so that they are available for
    // injection via @Named("play.configuration.name")
    private void bindConfiguration(Configuration configuration) {
        for (Map.Entry<String, ConfigValue> entry : configuration.entrySet()) {
            ConfigValue value = entry.getValue();
            Object unwrapped = value.unwrapped();
            String key = entry.getKey();
            ConfigValueType configValueType = value.valueType();

            Named namedKey = Names.named(key);
            if (!configValueType.equals(ConfigValueType.NULL)) {
                String unwrappedAsString = unwrapped.toString();

                if (configValueType.equals(ConfigValueType.BOOLEAN)) {
                    bindConstant().annotatedWith(namedKey).to(Boolean.valueOf(unwrappedAsString));
                } else if (configValueType.equals(ConfigValueType.STRING)) {
                    bindConstant().annotatedWith(namedKey).to(unwrappedAsString);
                } else if (configValueType.equals(ConfigValueType.NUMBER)) {
                    if (unwrapped instanceof Integer) {
                        bindConstant().annotatedWith(namedKey).to((Integer) (unwrapped));
                    } else if (unwrapped instanceof Long) {
                        bindConstant().annotatedWith(namedKey).to((Long) (unwrapped));
                    } else if (unwrapped instanceof Double) {
                        bindConstant().annotatedWith(namedKey).to((Double) (unwrapped));
                    } else if (unwrapped instanceof Float) {
                        bindConstant().annotatedWith(namedKey).to((Float) (unwrapped));
                    } else if (unwrapped instanceof BigDecimal) {
                        bindConstant().annotatedWith(namedKey).to(((BigDecimal) (unwrapped)).doubleValue());
                    } else {
                        logger.warn("unknown type={} for key={}", unwrapped.getClass(), key);
                    }
                } else if (configValueType.equals(ConfigValueType.LIST)) {
                    List<?> configs = (List) unwrapped;
                    if (!configs.isEmpty() && configs.get(0) instanceof Map) {
                        bindConstant().annotatedWith(namedKey).to(unwrappedAsString);
                    } else {
                        String valuesCommaSeparated = configs.stream().map(String::valueOf).collect(joining(","));
                        bindConstant().annotatedWith(namedKey).to(valuesCommaSeparated);
                    }
                } else if (configValueType.equals(ConfigValueType.NULL)) {
                    logger.warn("skipped binding NULL value for key={}", key);
                } else if (configValueType.equals(ConfigValueType.OBJECT)) {
                    logger.info("bind object value={} key={} as string", unwrapped.getClass(), key);
                    bindConstant().annotatedWith(namedKey).to(unwrappedAsString);
                } else {
                    logger.warn("unknown type={} for key={} so bind as string", unwrapped.getClass(), key);
                    bindConstant().annotatedWith(namedKey).to(unwrappedAsString);
                }
            }
        }
    }
}
