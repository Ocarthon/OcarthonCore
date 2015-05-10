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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TimerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testStartException() throws Exception {
        Timer timer = new Timer();
        timer.start();

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Timer has already been started");
        timer.start();
    }

    @Test
    public void testStopException() throws Exception {
        Timer timer = new Timer();

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Timer has to be started first");
        timer.stop();
    }

    @Test
    public void testTimerFunction() throws Exception {
        Timer timer = new Timer();

        long startTime = System.nanoTime();
        timer.start();

        Thread.sleep(10);

        timer.stop();
        long elapsedTime = System.nanoTime() - startTime;
        assertTrue(Math.abs(elapsedTime - timer.getElapsedTime()) / 1000000f < 1f);
    }

    @Test
    public void testReturnValue() throws Exception {
        Timer timer = new Timer();

        timer.start();
        Thread.sleep(10);

        assertEquals((int) timer.getElapsedTime() / 1000000, timer.getElapsedTime(TimeUnit.MILLISECONDS), 2);
    }
}
