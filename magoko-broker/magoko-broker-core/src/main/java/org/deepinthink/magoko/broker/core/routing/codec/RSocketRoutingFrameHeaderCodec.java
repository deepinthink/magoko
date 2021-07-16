/*
 * Copyright (c) 2021-present deepinthink. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deepinthink.magoko.broker.core.routing.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingFrameType;

public final class RSocketRoutingFrameHeaderCodec {

  public static final int FRAME_TYPE_SIZE = Short.BYTES; // 2bytes
  public static final int FLAGS_BITS = 10; // 10 bit flags
  public static final int FRAME_FLAGS_MASK = 0b0000_0011_1111_1111;

  public static ByteBuf encode(
      ByteBufAllocator allocator, RSocketRoutingFrameType frameType, int flags) {
    int frameId = frameType.getId() << FLAGS_BITS;
    short typeAndFlags = (short) (frameId | (short) flags);
    return allocator.buffer().writeShort(typeAndFlags);
  }

  public static int flags(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    short typeAndFlags = byteBuf.readShort();
    byteBuf.resetReaderIndex();
    return typeAndFlags & FRAME_FLAGS_MASK;
  }

  public static RSocketRoutingFrameType frameType(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    short typeAndFlags = byteBuf.readShort();
    byteBuf.resetReaderIndex();
    return RSocketRoutingFrameType.from(typeAndFlags >> FLAGS_BITS);
  }

  private RSocketRoutingFrameHeaderCodec() {}
}
