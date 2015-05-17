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
import static org.junit.Assert.assertNull;

public class HashTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private byte[] data = "LoremIpsum".getBytes();

    @Test
    public void testHash() throws Exception {
        // only for coverage
        ConstructorUtil.invokeConstructor(Hash.class, new Class[]{});
    }

    @Test
    public void testHashAlgorithmNPE() throws Exception {
        exception.expect(NullPointerException.class);
        Hash.hash(null, data);
    }

    @Test
    public void testHashDataNPE() throws Exception {
        exception.expect(NullPointerException.class);
        Hash.hash("SHA-128", null);
    }

    @Test
    public void testHashNoSuchAlgorithm() {
        assertNull(Hash.hash("NonExistingAlgorithm", data));
    }

    @Test
    public void testMd5() throws Exception {
        assertArrayEquals(Hex.toHexString(Hash.md5(data)).getBytes(),
                "d8acb9272677b376f85fde36b8a3e762".toLowerCase().getBytes());
    }

    @Test
    public void testSha() throws Exception {
        assertArrayEquals(Hex.toHexString(Hash.sha1(data)).getBytes(),
                "37de7433db93c025a175cc1417dcada8fff85366".toLowerCase().getBytes());
    }

    @Test
    public void testSha256() throws Exception {
        assertArrayEquals(Hex.toHexString(Hash.sha256(data)).getBytes(),
                ("8b4d4fda010ef93e765dea76b3ec0b5fa33a96a4d5c5973f27a03b" +
                        "d33272599f").toLowerCase().getBytes());
    }

    @Test
    public void testSha512() throws Exception {
        assertArrayEquals(Hex.toHexString(Hash.sha512(data)).getBytes(),
                ("d78e22a2b89c2f1a7e467e3254fdd2db7df5d66730a261fab62f2b0" +
                        "95109ba53a017fbdf5ad856d7ca938d0f37787d18fd375ec" +
                        "90619b7709f0baca091db4b51").toLowerCase().getBytes());
    }
}
