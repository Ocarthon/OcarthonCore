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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTCPClientServerTest implements TCPListener {
    private final String[] entry = new String[]{""};

    @Test
    public void testClientServerCommunication() throws Exception {
        TCPServer server = new TCPServer(true);
        server.addCustomCodec(p -> {
        });
        server.setListener(new TCPListener() {
            @Override
            public void onClientConnect(ChannelHandlerContext ctx) {
            }

            @Override
            public void onClientDisconnect(ChannelHandlerContext ctx) {
            }

            @Override
            public void onMessageReceived(ChannelHandlerContext ctx, Object message) {
            }

            @Override
            public void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                System.out.println(cause.toString());
            }
        });
        server.initBootstrap();
        assertTrue(server.bind(24313));

        assertEquals(0, server.getClients().size());

        TCPClient client = new TCPClient();
        client.addCustomCodec(p -> {
        });
        client.setListener(this);
        client.initBootstrap();

        assertTrue(client.connect("127.0.0.1", 24313));

        Thread.sleep(1000);
        server.getClients().writeAndFlush(Unpooled.wrappedBuffer("Test".getBytes()));

        synchronized (entry) {
            if (entry[0].isEmpty()) {
                entry.wait();
            }
        }
        assertFalse(entry[0].isEmpty());

        client.getChannel().close();
        client.release();

        server.shutdown();
    }

    @Override
    public void onClientConnect(ChannelHandlerContext ctx) {
    }

    @Override
    public void onClientDisconnect(ChannelHandlerContext ctx) {
    }

    @Override
    public void onMessageReceived(ChannelHandlerContext ctx, Object message) {
        String string = ((ByteBuf) message).toString(StandardCharsets.UTF_8);
        
        synchronized (entry) {
            entry[0] = string;
            entry.notifyAll();
        }
    }

    @Override
    public void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.toString());
    }
}
