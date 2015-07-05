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

package de.ocarthon.core.thread;

import de.ocarthon.core.utility.Timer;

import java.util.concurrent.TimeUnit;

/**
 * A utility class that provides a ticked thread. {@link #execute()} gets
 * called the number of times specified in the constructor
 * {@link #TicketThread(String, int)}.
 */
public abstract class TicketThread extends LoopedThread {
    /**
     * holds the number of ticks the thread should run in a seconds
     *
     * @see #getTickCount()
     */
    private final int ticks;

    /**
     * holds the time of a tick in milliseconds
     *
     * @see #getTickTime()
     */
    private final float tickTime;

    /**
     * holds the number of microseconds that were skipped
     */
    private int microSecondsToWait = 0;

    /**
     * timer instance to measure the time needed to run {@link #execute()}
     */
    private Timer timer = new Timer();

    /**
     * Creates a new instance of TickedThread.
     *
     * @param ticks the number of timer the thread should run in a seconds
     */
    public TicketThread(String name, int ticks) {
        super(name);
        this.ticks = ticks;
        this.tickTime = 1000f / ((float) ticks);
    }

    /**
     * Handles execution of {@link #execute()} and handles the delay
     * between executions.<p>
     * The time needed to run {@link #execute()} is measured and compared to
     * the time of a tick. If the total time for a tick is bigger, the time
     * needed to synchronize the thread to the tick will be waited
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Override
    public void internalExecute() throws InterruptedException {
        timer.start();
        execute();
        timer.stop();

        // elapsed time in microseconds
        long elapsedTime = timer.getElapsedTime(TimeUnit.MICROSECONDS);

        // elapsed time in milliseconds. rounded of for precision
        long millis = (long) Math.floor(elapsedTime / 1000f);

        if (millis < tickTime) {
            microSecondsToWait += elapsedTime % 1000;

            if (microSecondsToWait >= 1000) {
                microSecondsToWait -= 1000;
                millis--;
            }

            sleep((long) (tickTime - millis));
        }
    }

    /**
     * Returns the number of ticks the thread runs in a seconds
     *
     * @return number of ticks in a second
     */
    public int getTickCount() {
        return ticks;
    }

    /**
     * Returns the time needed for a tick to achieve the specified number of
     * ticks in a seconds
     *
     * @return time of a tick in milliseconds
     */
    public double getTickTime() {
        return tickTime;
    }
}
