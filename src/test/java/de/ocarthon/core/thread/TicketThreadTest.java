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

import static org.junit.Assert.assertEquals;

public class TicketThreadTest {
    private int executions = 0;

    private TicketThread defThread = new TicketThread(20) {
        @Override
        public void execute() throws InterruptedException {
        }
    };

    @Test
    public void testTickedThread() throws Exception {
        TicketThread thread = new TicketThread(10) {
            @Override
            public void execute() throws InterruptedException {
                executions++;
                sleep(1);
            }
        };

        thread.start();
        Thread.sleep(200);
        thread.stopThread();

        assertEquals(2, executions, 2);
    }

    @Test
    public void testTickCount() throws Exception {
        assertEquals(20, defThread.getTickCount());
    }

    @Test
    public void testTickTime() throws Exception {
        assertEquals(50, ((int) defThread.getTickTime()));
    }
}
