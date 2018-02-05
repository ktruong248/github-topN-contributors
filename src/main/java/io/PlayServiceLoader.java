package io;

import play.api.inject.guice.GuiceableModule;
import play.api.inject.guice.GuiceableModule$;
import play.api.routing.Router;
import play.inject.guice.GuiceApplicationLoader;

import java.util.Arrays;

import static play.inject.Bindings.bind;

/**
 * Custom Play application loader that enables applications to specify routes using the routing DSL.
 *
 * @see <a href="https://www.playframework.com/documentation/2.5.x/JavaRoutingDsl#providing-a-di-router-with-guice">
 * Providing a DI router with Guice
 * </a>
 */
public class PlayServiceLoader extends GuiceApplicationLoader {
    @Override
    protected GuiceableModule[] overrides(Context context) {
        GuiceableModule routerBinding = GuiceableModule$.MODULE$
                .fromPlayBinding(bind(Router.class).toProvider(RouterProvider.class).eagerly());
        GuiceableModule[] defaultBindings = super.overrides(context);

        GuiceableModule[] overrides = Arrays.copyOf(defaultBindings, defaultBindings.length + 1);
        overrides[overrides.length - 1] = routerBinding;
        return overrides;
    }
}
