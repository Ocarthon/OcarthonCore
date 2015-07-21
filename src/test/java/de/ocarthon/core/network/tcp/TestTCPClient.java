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

package de.ocarthon.core.network.tcp;

import de.ocarthon.core.utility.reflection.MethodUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestTCPClient {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testConstructors() {
        new TCPClient(true);

        NioEventLoopGroup group = new NioEventLoopGroup();

        TCPClient client1 = new TCPClient();
        assertNotEquals(group, client1.getEventLoopGroup());

        TCPClient client2 = new TCPClient(group, true);
        assertEquals(group, client2.getEventLoopGroup());
    }

    @Test
    public void testConnectIllegalStateException() throws Exception {
        TCPClient client = new TCPClient();
        exception.expect(IllegalStateException.class);
        exception.expectMessage("TCPClient#setup() must be called first!");
        client.connect(null, -1);
    }

    @Test
    public void testClientHandlerExceptionCaught() {
        Object clientHandler = new TCPClient().getClientHandler();
        MethodUtil.invoke(clientHandler, "exceptionCaught", new Class[]{
                ChannelHandlerContext.class, Throwable.class}, null, null);

    }
}
