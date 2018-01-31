package io.wiser.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.wiser.service.controller.GithubStatsController;
import play.Configuration;
import play.Environment;
import play.mvc.Result;
import play.routing.RoutingDsl;

import java.util.function.Function;

public class RoutesModule extends AbstractModule {
    private static final String SERVICE_URI = "/service";

    private final Configuration configuration;

    public RoutesModule(@SuppressWarnings("unused") Environment environment, Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
    }

    @Provides
    RoutingDsl provideRoutingDsl(GithubStatsController adminController) {
        return new RoutingDsl()
                .GET(buildUri("/:orgs/top_contributors")).routeTo(adminController::topContributors);
    }

    private String buildUri(String path) {
        return SERVICE_URI + path;
    }
}