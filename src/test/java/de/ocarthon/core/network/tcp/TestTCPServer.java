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
import org.junit.Test;

public class TestTCPServer {

    @Test
    public void testServerChannel() {
        TCPServer server = new TCPServer(true);
        server.getServerChannel();
    }

    @Test
    public void testServerHandler() {
        TCPServer server = new TCPServer(true);
        Object handler = server.getServerHandler();

        MethodUtil.invoke(handler, "exceptionCaught", new Class[]{
                ChannelHandlerContext.class, Throwable.class}, null, null);

        MethodUtil.invoke(handler, "channelRead", new Class[]{
                ChannelHandlerContext.class, Object.class}, null, null);
    }
}
