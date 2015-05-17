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

import de.ocarthon.core.utility.reflection.ConstructorUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HexTest {
    public byte[] content = "Test".getBytes();
    public String hexString = "A25BC143";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testToHexString() throws Exception {
        // coverage
        ConstructorUtil.invokeConstructor(Hex.class, new Class[]{});

        assertArrayEquals(Hex.fromHexString(Hex.toHexString(content)),
                content);
    }

    @Test
    public void testHexLetters() throws Exception {
        assertArrayEquals(Hex.toHexString(Hex.fromHexString(hexString), true).getBytes(),
                hexString.getBytes());

        assertArrayEquals(Hex.toHexString(Hex.fromHexString(hexString.toLowerCase())).getBytes(),
                hexString.toLowerCase().getBytes());
    }

    @Test
    public void testUnevenStringLength() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("input needs to be even");
        Hex.fromHexString("A");
    }

    @Test
    public void testFromHexStringException() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("contains illegal character");
        Hex.fromHexString("HA");
    }

    @Test
    public void testToHex() throws Exception {
        assertEquals(Hex.toHex(10), "a");
    }
}
