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

import de.ocarthon.core.utility.reflection.MethodUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class HttpClient {
    private static NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private static HttpDataFactory httpDataFactory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private static ChannelFactory<Channel> channelFactory =
            new ReflectiveChannelFactory<>(NioSocketChannel.class);
    private static Bootstrap defaultHttpBootstrap;
    private final String[] result = {null};
    private String scheme;
    private String host;
    private int port;
    private Bootstrap bootstrap;
    private Channel channel;
    private boolean useUntrustedConnections = false;
    private SslContext sslCtx;
    private boolean forceReconnect = false;

    public HttpClient(String scheme, String host) {
        this(scheme, host, -1);
    }

    public HttpClient(URI uri) {
        this(uri.getScheme(), uri.getHost(), uri.getPort());
    }

    public HttpClient(String scheme, String host, int port) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;

        checkPortAndScheme();
    }

    private static Bootstrap defaultBootstrap() {
        if (defaultHttpBootstrap == null) {
            defaultHttpBootstrap = new Bootstrap();
            defaultHttpBootstrap.group(eventLoopGroup);
            defaultHttpBootstrap.option(ChannelOption.TCP_NODELAY, true);
            defaultHttpBootstrap.option(ChannelOption.ALLOCATOR,
                    PooledByteBufAllocator.DEFAULT);
            defaultHttpBootstrap.channelFactory(channelFactory);
        }
        return defaultHttpBootstrap;
    }

    public static Bootstrap createBootstrap() {
        Bootstrap bootstrap = defaultBootstrap().clone();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();

                p.addLast("codec", new HttpClientCodec());
                p.addLast("chunkedWriter", new ChunkedWriteHandler());
                p.addLast("gzip", new HttpContentDecompressor());
            }
        });

        return bootstrap;
    }

    public static Bootstrap createBootstrapSsl(SslContext sslCtx) {
        Bootstrap bootstrap = defaultBootstrap().clone();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();

                p.addLast("ssl", sslCtx.newHandler(ch.alloc()));

                p.addLast("codec", new HttpClientCodec());
                p.addLast("chunkedWriter", new ChunkedWriteHandler());
                p.addLast("gzip", new HttpContentDecompressor());
            }
        });

        return bootstrap;
    }

    public synchronized String postRequest(String query, List<Map.Entry<String, String>>
            postParameters) {
        return postRequest(query, postParameters, null, null, null);
    }

    public synchronized String postRequest(String query, List<Map.Entry<String, String>>
            postParameters, String filePostName, File file, String mime) {
        if (bootstrap == null) {
            setupBootstrap();
        }

        if (channel == null || forceReconnect) {
            ChannelFuture cf = bootstrap.connect(host, port);
            forceReconnect = false;
            cf.awaitUninterruptibly();
            channel = cf.channel();

            channel.pipeline().addLast("handler", new SimpleChannelInboundHandler<HttpObject>() {
                @Override
                protected void messageReceived(ChannelHandlerContext ctx, HttpObject msg)
                        throws Exception {
                    if (msg instanceof HttpResponse) {
                        HttpResponse response = ((HttpResponse) msg);
                        String connection = (String)
                                response.headers().get(HttpHeaderNames.CONNECTION);
                        if (connection != null && connection.equalsIgnoreCase(
                                HttpHeaderValues.CLOSE.toString())) forceReconnect = true;
                    }

                    if (msg instanceof HttpContent) {
                        HttpContent chunk = (HttpContent) msg;
                        String message = chunk.content().toString(CharsetUtil.UTF_8);

                        if (!message.isEmpty()) {
                            result[0] = message;

                            synchronized (result) {
                                result.notify();
                            }
                        }
                    }
                }
            });
        }
        boolean isFileAttached = file != null && file.canRead();
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
                HttpMethod.POST, scheme + "://" + host + ":" + port + "/" + query);
        HttpPostRequestEncoder bodyReqEncoder;
        try {
            bodyReqEncoder = new HttpPostRequestEncoder(httpDataFactory, request,
                    isFileAttached);

            for (Map.Entry<String, String> entry : postParameters) {
                bodyReqEncoder.addBodyAttribute(entry.getKey(), entry.getValue());
            }

            if (isFileAttached) {
                if (mime == null) mime = "application/octet-stream";

                bodyReqEncoder.addBodyFileUpload(filePostName, file, mime, false);
            }

            HttpHeaders headers = request.headers();
            headers.set(HttpHeaderNames.HOST, host);
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            headers.set(HttpHeaderNames.USER_AGENT, "OcarthonCore HttpClient");
            request = bodyReqEncoder.finalizeRequest();
        } catch (Exception e) {
            throw new NullPointerException("key or value is empty or null");
        }

        channel.write(request);

        if (bodyReqEncoder.isChunked()) {
            channel.write(bodyReqEncoder);
        }
        channel.flush();

        synchronized (result) {
            try {
                result.wait();
            } catch (InterruptedException e) {
                return null;
            }
        }

        return result[0];
    }

    private void checkPortAndScheme() {
        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new IllegalArgumentException("only http and https are supported!");
        }

        if (port == -1) {
            switch (scheme) {
                case "http":
                    port = 80;
                    return;

                case "https":
                    port = 443;
            }
        }

        if (port < 0 || port >= 65536) {
            throw new IllegalArgumentException("invalid port: " + port +
                    " (must be between 0 and 65535))");
        }
    }

    public void allowUntrustedConnections() {
        useUntrustedConnections = true;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private void setupBootstrap() {
        if (scheme.equals("https")) {
            setupSslContext();
            bootstrap = createBootstrapSsl(sslCtx);
        } else {
            bootstrap = createBootstrap();
        }
    }

    private void setupSslContext() {
        if (useUntrustedConnections) {
            sslCtx = (SslContext) MethodUtil.invoke(SslContext.class, "newClientContext",
                    new Class[]{TrustManagerFactory.class}, InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = (SslContext) MethodUtil.invoke(SslContext.class, "newClientContext",
                    null);
        }
    }
}
