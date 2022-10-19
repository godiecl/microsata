/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef;

import java.util.concurrent.TimeUnit;

/**
 * The TimeUtils.
 *
 * @author Diego Urrutia-Astorga.
 */
public final class Timer {

    /**
     * The starting time.
     */
    private final long start = System.nanoTime();

    /**
     * Private constructor.
     */
    private Timer() {
        // Nothing here.
    }

    /**
     * Calculate the diff between the now and the start.
     *
     * @return the milliseconds.
     */
    public long millis() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return String.valueOf(this.millis());
    }

    /**
     * Start the timer.
     *
     * @return the new instance.
     */
    public static Timer start() {
        return new Timer();
    }

}
