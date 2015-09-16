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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertTrue;

public class GzipCompressionTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGzipCompression() throws Exception {
        new GzipCompression();
        String orig = "Test123";

        ByteArrayOutputStream bois = new ByteArrayOutputStream();
        GzipCompression.compress(orig, bois);
        ByteArrayInputStream bais = new ByteArrayInputStream(bois.toByteArray());
        String result = GzipCompression.decompress(bais);
        assertTrue(orig.equals(result));
    }

}