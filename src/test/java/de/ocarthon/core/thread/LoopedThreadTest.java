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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoopedThreadTest {
    private boolean postExecuted = false;
    private int count = 0;

    @Test
    public void testLoopedThread() throws Exception {
        LoopedThread thread = new DefaultLoopedThread("Test");
        assertFalse(thread.isRunning());

        thread.start();
        assertTrue(thread.isRunning());
        assertFalse(thread.shouldStop());

        Thread.sleep(15);
        thread.stopThread();
        assertTrue(thread.shouldStop());

        Thread.sleep(20);
        assertFalse(thread.isRunning());
    }

    @Test
    public void testLoopedThreadInterrupt() throws Exception {
        LoopedThread thread = new DefaultLoopedThread("Test") {
            @Override
            public void postDispose() {
                postExecuted = true;
            }
        };
        thread.start();
        assertTrue(thread.isRunning());

        thread.interrupt();
        Thread.sleep(30);
        assertFalse(thread.isRunning());
        assertTrue(postExecuted);
    }

    @Test
    public void testLoopedThreadInterruptForced() throws Exception {
        LoopedThread thread = new LoopedThread("Test") {
            @Override
            public void execute() throws InterruptedException {
                throw new InterruptedException();
            }
        };
        thread.start();
        Thread.sleep(20);

        assertFalse(thread.isRunning());
    }

    @Test
    public void testLoopedThreadWait() throws Exception {
        LoopedThread thread = new LoopedThread("Test", 2) {
            @Override
            public void execute() throws InterruptedException {
                count++;
            }
        };
        thread.start();
        Thread.sleep(4);
        thread.stopThread();

        assertTrue(count >= 1);
    }

    private class DefaultLoopedThread extends LoopedThread {

        public DefaultLoopedThread(String name) {
            super(name);
        }

        @Override
        public void execute() {
            try {
                sleep(20);
            } catch (InterruptedException e) {
                // Unhandled for test
            }
        }
    }
}
