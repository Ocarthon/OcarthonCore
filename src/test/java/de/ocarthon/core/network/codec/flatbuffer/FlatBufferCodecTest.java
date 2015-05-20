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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FlatBufferCodecTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testFlatBufferCodecIllegalArgumentException() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid FlatBuffer table");
        new FlatBufferCodec(TableMock.class);
    }

    @Test
    public void testFlatBufferCodec() throws Exception {
        FlatBufferBuilder fbb = new FlatBufferBuilder(32);
        int fbTest = FBTest.createFBTest(fbb, 1337);
        FBTest.finishFBTestBuffer(fbb, fbTest);

        FlatBufferCodec codec = new FlatBufferCodec(FBTest.class);
        List<Object> list = new ArrayList<>();

        codec.encode(null, fbb, list);
        ByteBuf buf = ((ByteBuf) list.get(0));
        list.clear();

        codec.decode(null, buf, list);
        FBTest fb = ((FBTest) list.get(0));

        assertEquals(1337, fb.test());
    }

    public static class TableMock extends Table {
    }

    public static class FBTest extends Table {
        public static FBTest getRootAsFBTest(ByteBuffer _bb) {
            return getRootAsFBTest(_bb, new FBTest());
        }

        public static FBTest getRootAsFBTest(ByteBuffer _bb, FBTest obj) {
            _bb.order(ByteOrder.LITTLE_ENDIAN);
            return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb));
        }

        public static int createFBTest(FlatBufferBuilder builder,
                                       int test) {
            builder.startObject(1);
            FBTest.addTest(builder, test);
            return builder.endObject();
        }

        public static void startFBTest(FlatBufferBuilder builder) {
            builder.startObject(1);
        }

        public static void addTest(FlatBufferBuilder builder, int test) {
            builder.addInt(0, test, 0);
        }

        public static void finishFBTestBuffer(FlatBufferBuilder builder, int offset) {
            builder.finish(offset);
        }

        public FBTest __init(int _i, ByteBuffer _bb) {
            bb_pos = _i;
            bb = _bb;
            return this;
        }

        public int test() {
            int o = __offset(4);
            return o != 0 ? bb.getInt(o + bb_pos) : 0;
        }
    }

}
