package io.shutdown;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ApplicationShutdownHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationShutdownHandler.class);

    private final List<ShutdownHook> shutdownHooks;

    public ApplicationShutdownHandler(List<ShutdownHook> shutdownHooks) {
        this.shutdownHooks = ImmutableList.copyOf(shutdownHooks);
    }

    public void stop() {
        logger.info("Notifying shutdown hooks of shutdown");
        for (ShutdownHook shutdownHook : shutdownHooks) {
            try {
                logger.info("Notifying {}", shutdownHook);
                shutdownHook.onStop();
            } catch (Exception e) {
                logger.error(String.format("Unable to execute shutdown hook: %s", shutdownHook), e);
            }
        }
    }

    public interface ShutdownHook {
        void onStop();
    }
}