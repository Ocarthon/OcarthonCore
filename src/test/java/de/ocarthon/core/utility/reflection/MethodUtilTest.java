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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodUtilTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testInvokeNullPointerException() throws Exception {
        new MethodUtil();

        exception.expect(NullPointerException.class);
        exception.expectMessage("the given object is null");
        MethodUtil.invoke(null, "", null);
    }

    @Test
    public void testInvokeNoSuchMethodException() {
        exception.expect(RuntimeException.class);
        MethodUtil.invoke(BigInteger.class, "noMethod", new Class[]{});
    }

    @Test
    public void testInvokeClass() throws Exception {
        Object result = MethodUtil.invoke(Integer.class, "valueOf", new Class[]{String.class},
                "1");
        assertTrue(result instanceof Integer);
        assertTrue(((Integer) result) == 1);
    }

    @Test
    public void testInvokeObject() throws Exception {
        Object result = MethodUtil.invoke(BigInteger.ONE, "add", new Class[]{BigInteger.class},
                BigInteger.TEN);
        assertTrue(result instanceof BigInteger);
        assertEquals(((BigInteger) result).intValue(), 11);
    }

    @Test
    public void testExistsNullPointerException() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("class or name of the method is null!");
        MethodUtil.exists(null, null);
    }

    @Test
    public void testExistsTrue() throws Exception {
        assertTrue(MethodUtil.exists(Integer.class, "valueOf", String.class));
    }

    @Test
    public void testExistsFalse() throws Exception {
        assertFalse(MethodUtil.exists(Integer.class, "noMethod", String.class));
    }
}
