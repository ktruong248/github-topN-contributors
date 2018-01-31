package io.wiser.common;

/**
 * simple implementation of Sleeper using Thread.sleep
 */
public class SystemSleeper implements Sleeper {
    @Override
    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}