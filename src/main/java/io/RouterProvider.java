package io;

import play.api.routing.Router;
import play.routing.RoutingDsl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Builds the Play Router object from application-provided routes and admin routes common to all services.
 */
@Singleton
class RouterProvider implements Provider<Router> {
    private final RoutingDsl routes;

    @Inject
    RouterProvider(RoutingDsl routes) {
        this.routes = routes;
    }

    @Override
    public Router get() {
        return routes.build().asScala();
    }
}
