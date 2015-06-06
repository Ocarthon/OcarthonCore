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

package de.ocarthon.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HttpClientTest {
    private static final String HOST = "ocarthon.de";
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private HttpClient defaultHttpClient;
    private List<Map.Entry<String, String>> postParameters;
    private ByteBuf testFile;

    @Before
    public void setUp() throws Exception {
        postParameters = new ArrayList<>();
        postParameters.add(new AbstractMap.SimpleEntry<>("t", "123"));
        defaultHttpClient = new HttpClient("https", HOST);
        testFile = Unpooled.wrappedBuffer("Test".getBytes());
    }

    @Test
    public void testConstructor1() throws Exception {
        HttpClient client = new HttpClient("https", HOST);
        assertEquals("https", client.getScheme());
        assertEquals(HOST, client.getHost());
        assertEquals(443, client.getPort());

        client = new HttpClient("http", HOST);
        assertEquals("http", client.getScheme());
        assertEquals(HOST, client.getHost());
        assertEquals(80, client.getPort());
    }

    @Test
    public void testConstructor2() throws Exception {
        HttpClient client = new HttpClient("http", HOST, 8080);
        assertEquals("http", client.getScheme());
        assertEquals(HOST, client.getHost());
        assertEquals(8080, client.getPort());
    }

    @Test
    public void testConstructor3() throws Exception {
        URI uri = new URI("https://ocarthon.de");
        HttpClient client = new HttpClient(uri);
        assertEquals("https", client.getScheme());
        assertEquals(HOST, client.getHost());
        assertEquals(443, client.getPort());
    }

    @Test
    public void testConstructorIllegalArgumentPort() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("invalid port:");
        new HttpClient("https", HOST, 65536);
    }

    @Test
    public void testConstructorIllegalArgumentScheme() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("only http and https are supported!");
        new HttpClient("noScheme", HOST, -1);
    }

    @Test
    public void testAllowUntrustedConnections() throws Exception {
        HttpClient client = new HttpClient("https", HOST);
        client.allowUntrustedConnections();

        String result = client.postRequest("index.html", postParameters);
        assertNotNull(result);
        assertTrue(result.trim().charAt(0) == '<');
    }

    @Test
    public void testPostRequest() throws Exception {
        HttpClient client = new HttpClient("http", HOST);

        String result = client.postRequest("index.html", postParameters);
        assertNotNull(result);
        assertTrue(result.trim().charAt(0) == '<');
    }

    @Test
    public void testPostRequestSsl() throws Exception {
        String result = defaultHttpClient.postRequest("index.html", postParameters);
        assertNotNull(result);
        assertTrue(result.trim().charAt(0) == '<');
    }

    @Test
    public void testPostRequestFileUpload() throws Exception {
        String result = defaultHttpClient.postRequest("index.html", postParameters,
                "test", "test.txt", testFile, null);
        assertNotNull(result);
        assertTrue(result.trim().charAt(0) == '<');
    }

    @Test
    public void testPostRequestNullPointerException() throws Exception {
        List<Map.Entry<String, String>> postParameters = new ArrayList<>();
        postParameters.add(new AbstractMap.SimpleEntry<>(null, ""));

        exception.expect(NullPointerException.class);
        exception.expectMessage("key or value is empty or null");
        defaultHttpClient.postRequest("index.html", postParameters);
    }

    @Test
    public void testPostRequestInterruptedException() throws Exception {
        Thread.currentThread().interrupt();
        String result = defaultHttpClient.postRequest("index.html", postParameters);
        assertNull(result);
    }

}
