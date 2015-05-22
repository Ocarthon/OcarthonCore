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

package de.ocarthon.core.network.codec.flatbuffer;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

public class FlatBufferCodec extends MessageToMessageCodec<ByteBuf, FlatBufferBuilder> {
    private Method method;

    public FlatBufferCodec(Class<? extends Table> rootClass) {
        String methodName = "getRootAs" + rootClass.getSimpleName();

        try {
            method = rootClass.getDeclaredMethod(methodName, ByteBuffer.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Invalid FlatBuffer table");
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FlatBufferBuilder msg, List<Object> out) throws Exception {
        out.add(Unpooled.copiedBuffer(msg.sizedByteArray()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(method.invoke(null, msg.nioBuffer()));
    }
}
