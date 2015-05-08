/*
 *    Copyright 2015 Ocarthon (Philip Standt)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.ocarthon.core.utility;

import java.util.concurrent.TimeUnit;

public class Timer {
    private long startTime = 0;
    private long elapsedTime = 0;

    public Timer() {
    }

    /**
     * Starts the timer. You have to call {@link #stop()} before you can start the timer
     * again
     *
     * @throws IllegalStateException when the timer has already been started
     */
    public void start() {
        if (startTime != 0) {
            throw new IllegalStateException("Timer has already been started");
        } else {
            startTime = System.nanoTime();
            elapsedTime = -1;
        }
    }

    /**
     * Stops the timer.
     *
     * @throws IllegalStateException when the timer has not been started
     */
    public void stop() {
        if (startTime == 0) {
            throw new IllegalStateException("Timer has to be started first");
        } else {
            elapsedTime = System.nanoTime() - startTime;
            startTime = 0;
        }
    }

    /**
     * Returns the elapsed time between {@link #start()} and {@link #stop()} in
     * nanoseconds. If the timer has not been stopped, the time between the
     * calls of {@link #start()} and this method will be returned.
     *
     * @return the elapsed time in nanoseconds
     * @see #getElapsedTime(TimeUnit)
     */
    public long getElapsedTime() {
        return getElapsedTime(TimeUnit.NANOSECONDS);
    }

    /**
     * Returns the elapsed time between {@link #start()} and {@link #stop()} If
     * the timer has not been stopped, the time between the calls of {@link #start()}
     * and this method
     *
     * @param timeUnit the unit, in that the elapsed time is returned
     * @return the elapsed time
     * @see #getElapsedTime()
     */
    public long getElapsedTime(TimeUnit timeUnit) {
        if (elapsedTime == -1) {
            return timeUnit.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        } else {
            return timeUnit.convert(elapsedTime, TimeUnit.NANOSECONDS);
        }
    }
}
