package io.wiser.shutdown;

import io.wiser.common.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class EnterShuttingDownStatusHook implements ApplicationShutdownHandler.ShutdownHook {
    private static final long DEFAULT_DELAY_MS = 5000L;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationShutdownHandler.class);
    private final AtomicBoolean shuttingDown;
    private final Sleeper sleeper;
    private long delayDuration;

    @Inject
    EnterShuttingDownStatusHook(Configuration configuration, Sleeper sleeper,
                                @Named("ShuttingDownStatus") AtomicBoolean shuttingDown) {
        this.shuttingDown = shuttingDown;
        this.sleeper = sleeper;
        this.delayDuration = configuration.getMilliseconds("play.server.shutdownDelay", DEFAULT_DELAY_MS);
    }

    @Override
    public void onStop() {
        shuttingDown.set(true);
        logger.info("delay shutting down server for {} (ms) to allow current requests to finish", delayDuration);
        sleeper.sleep(delayDuration);
    }
}
