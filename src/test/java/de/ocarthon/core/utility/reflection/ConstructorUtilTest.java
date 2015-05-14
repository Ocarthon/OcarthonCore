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

package de.ocarthon.core.utility.reflection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class ConstructorUtilTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testInvokeConstructor() throws Exception {
        new ConstructorUtil();

        BigInteger integer = ConstructorUtil.invokeConstructor(BigInteger.class, new Class[]{String.class}, "1");
        assertEquals(1, integer.intValue());
    }

    @Test
    public void testInvokeConstructorNullPointerException() throws Exception {
        exception.expect(RuntimeException.class);
        ConstructorUtil.invokeConstructor(String.class, new Class[]{Integer.class});
    }

    @Test
    public void testInvokeConstructorNoArguments() throws Exception {
        String string = ConstructorUtil.invokeConstructor(String.class, null);
        assertEquals("", string);
    }
}
