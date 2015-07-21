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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.function.Consumer;

public class TCPClient {
    private final TCPClientHandler handler = new TCPClientHandler();
    private final int lengthBytes = 2;
    private final boolean useTls;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private Consumer<ChannelPipeline> pipelineCodec;
    private LengthFieldPrepender lengthPrepender = new LengthFieldPrepender(lengthBytes);

    public TCPClient(NioEventLoopGroup group, boolean useTls) {
        this.useTls = useTls;

        if (group != null) {
            this.group = group;
        } else {
            this.group = new NioEventLoopGroup();
        }
    }

    public TCPClient() {
        this(null, true);
    }

    public TCPClient(boolean useTls) {
        this(null, useTls);
    }

    public void initBootstrap() {
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        // TLS
                        if (useTls) {
                            p.addLast(SslContext.newClientContext(
                                    InsecureTrustManagerFactory.INSTANCE)
                                    .newHandler(ch.alloc()));
                        }

                        p.addLast(lengthPrepender);
                        p.addLast(new LengthFieldBasedFrameDecoder(65535 /* (2^16)-1 */, 0,
                                lengthBytes, 0, lengthBytes));

                        if (pipelineCodec != null) {
                            pipelineCodec.accept(p);
                        }

                        p.addLast(handler);
                    }
                });
    }

    public boolean connect(String host, int port) throws InterruptedException {
        if (bootstrap != null) {
            ChannelFuture cf = bootstrap.connect(host, port);
            cf.sync();

            if (cf.isSuccess()) {
                this.channel = cf.channel();
            }

            return cf.isSuccess();
        } else {
            throw new IllegalStateException("TCPClient#setup() must be called first!");
        }
    }

    public void release() {
        if (channel != null && channel.isActive()) {
            channel.close().awaitUninterruptibly();
        }

        group.shutdownGracefully().awaitUninterruptibly();
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void addCustomCodec(Consumer<ChannelPipeline> pipelineCodec) {
        this.pipelineCodec = pipelineCodec;
    }

    public EventLoopGroup getEventLoopGroup() {
        return this.group;
    }

    public void setListener(TCPListener listener) {
        this.handler.setListener(listener);
    }

    public TCPClientHandler getClientHandler() {
        return this.handler;
    }

    private class TCPClientHandler extends ChannelHandlerAdapter {
        private TCPListener listener;

        public TCPClientHandler() {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (listener != null) listener.onMessageReceived(ctx, msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            if (listener != null) listener.onClientDisconnect(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            if (listener != null) listener.onClientConnect(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if (listener != null) listener.onExceptionCaught(ctx, cause);
        }

        public void setListener(TCPListener listener) {
            this.listener = listener;
        }
    }
}
