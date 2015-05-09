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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A utility class that provides a looped thread. {@link #execute()}
 * is getting called over and over until the Thread has been interrupted
 * or stopped via {@link #stopThread()}.<p>
 * <p>
 * For setup and cleanup, {@link #preInitialize()} and {@link #postDispose()}
 * are available:<pr>
 * {@link #preInitialize()} is getting called before the main loop and
 * {@link #postDispose()} after the thread has been stopped or
 * interrupted<p>
 * {@link #stopThread()} should be called to gracefully stop the thread. To see
 * the current state of the thread, {@link #isRunning()} can be called.
 *
 * @see #execute()
 * @see #preInitialize()
 * @see #postDispose()
 * @see #stopThread()
 * @see #isRunning()
 */
public abstract class LoopedThread extends Thread {
    /**
     * The time in ms that the loop waits after each execution of
     * {@link #execute()}.
     */
    private final int waitMs;

    /**
     * An atomic (thread-safe) value that holds the state of the thread.
     *
     * @see #isRunning()
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * An atomic (thread-safe) value that holds the state, whether or not the
     * thread / loop should be stopped after the next call of {@link #execute()}
     *
     * @see #shouldStop()
     */
    private AtomicBoolean shouldStop = new AtomicBoolean(false);

    /**
     * Creates a new LoopedThread instance. After each execution of {@link #execute()},
     * the thread waits for the set number of milliseconds. This value can not be changed
     * in this implementation.
     *
     * @param waitMs the time in ms that the thread waits after each
     *               execution of {@link #execute()}
     * @see #LoopedThread()
     */
    public LoopedThread(int waitMs) {
        this.waitMs = waitMs;
    }

    /**
     * Creates a new LoopedThread instance. {@link #execute()} gets called without a
     * delay between the calls.
     *
     * @see #LoopedThread(int)
     */
    public LoopedThread() {
        this.waitMs = 0;
    }

    /**
     * This method gets repeatedly called by the internal loop.<p>
     * If you need instances or variables, you should use
     * {@link #preInitialize()} to initialize them. For cleanup of
     * those instances, {@link #postDispose()} gets called after the internal
     * loop finished executing.<p>
     * In case of an {@link InterruptedException}, the thread will stop executing
     * and stop.
     *
     * @see #preInitialize()
     * @see #postDispose()
     */
    public abstract void execute() throws InterruptedException;

    /**
     * This method gets called before the internal loop begins to call
     * {@link #execute()}. It should be used to setup instances and
     * variables needed by {@link #execute()}.
     *
     * @see #postDispose()
     */
    public void preInitialize() {
    }

    /**
     * This method gets called after the internal loop exited. Here you should
     * do cleanup of your instances and variables you created in {@link #preInitialize()}.
     *
     * @see #preInitialize()
     */
    public void postDispose() {
    }

    /**
     * Handles execution of {@link #execute()} and handles the delay
     * between executions.<p>
     * For different timing behaviours, this method should be overwritten.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    private void internalExecute() throws InterruptedException {
        execute();

        if (waitMs > 0) {
            sleep(waitMs);
        }
    }

    @Override
    public synchronized final void start() {
        isRunning.set(true);
        super.start();
    }

    /**
     * Starts the internal loop. First, {@link #preInitialize()} gets executed
     * to setup needed instances. After that the internal loop starts, repeatedly
     * calling {@link #internalExecute()} to execute your code. The loop breaks if the
     * thread is stopped by {@link #stopThread()} or the thread is getting interrupted.
     * At last {@link #postDispose()} is called to cleanup instances if needed.
     *
     * @see #internalExecute()
     * @see #preInitialize()
     * @see #postDispose()
     */
    @Override
    public void run() {
        preInitialize();

        while (!shouldStop() && !interrupted()) {
            try {
                internalExecute();
            } catch (InterruptedException e) {
                break;
            }
        }

        postDispose();
        isRunning.set(false);
    }

    /**
     * Sets the internal state to stop the thread
     */
    public final void stopThread() {
        shouldStop.set(true);
    }

    /**
     * Function to check if the Thread is running
     *
     * @return true if the thread is running, false if not
     */
    public final boolean isRunning() {
        return isRunning.get();
    }

    /**
     * @return true if the Thread should stop but is still executing
     */
    public final boolean shouldStop() {
        return shouldStop.get();
    }
}
