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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.function.Consumer;

public class TCPServer {
    private final TCPServerHandler handler = new TCPServerHandler();
    private final boolean useTls;
    private final int lengthBytes = 2;
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap bootstrap;
    private Channel serverChannel;
    private Consumer<ChannelPipeline> pipelineCodec;
    private SslContext serverSslContext;
    private LengthFieldPrepender lengthPrepender = new LengthFieldPrepender(lengthBytes);

    public TCPServer(boolean useTls) {
        this.useTls = useTls;
    }

    public void initBootstrap() throws CertificateException, SSLException {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        this.serverSslContext = SslContext.newServerContext(cert.certificate(),
                cert.privateKey());

        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        if (useTls) {
                            p.addLast(serverSslContext.newHandler(ch.alloc()));
                        }

                        p.addLast(lengthPrepender);
                        p.addLast(new LengthFieldBasedFrameDecoder(65535, 0, lengthBytes,
                                0, lengthBytes));

                        if (pipelineCodec != null) {
                            pipelineCodec.accept(p);
                        }

                        p.addLast(handler);
                    }
                });
    }

    public boolean bind(int port) throws InterruptedException {
        return this.bind("0.0.0.0", port);
    }

    public boolean bind(String host, int port) throws InterruptedException {
        ChannelFuture cf = this.bootstrap.bind(host, port);
        cf.sync();
        serverChannel = cf.channel();
        return cf.isSuccess();
    }

    public void shutdown() {
        getClients().close().awaitUninterruptibly();
        serverChannel.close().awaitUninterruptibly();
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }

    public Channel getServerChannel() {
        return this.serverChannel;
    }

    public void addCustomCodec(Consumer<ChannelPipeline> pipelineCodec) {
        this.pipelineCodec = pipelineCodec;
    }

    public ChannelGroup getClients() {
        return handler.getChannels();
    }

    public void setListener(TCPListener listener) {
        this.handler.setListener(listener);
    }

    public TCPServerHandler getServerHandler() {
        return this.handler;
    }


    private class TCPServerHandler extends ChannelHandlerAdapter {
        private ChannelGroup channelGroup = new DefaultChannelGroup(
                GlobalEventExecutor.INSTANCE);
        private TCPListener listener;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            channelGroup.add(ctx.channel());
            if (listener != null) listener.onClientConnect(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            channelGroup.remove(ctx.channel());
            if (listener != null) listener.onClientDisconnect(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (listener != null) listener.onMessageReceived(ctx, msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if (listener != null) listener.onExceptionCaught(ctx, cause);
        }

        public void setListener(TCPListener listener) {
            this.listener = listener;
        }

        public ChannelGroup getChannels() {
            return this.channelGroup;
        }
    }
}
