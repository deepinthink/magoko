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
package org.deepinthink.magoko.boot.msocket.netty.codec;

import static org.deepinthink.magoko.boot.msocket.netty.codec.NettyMSocketFrameLengthCodec.FRAME_LENGTH_MASK;
import static org.deepinthink.magoko.boot.msocket.netty.codec.NettyMSocketFrameLengthCodec.FRAME_LENGTH_SIZE;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyMSocketFrameDecoder extends LengthFieldBasedFrameDecoder {

  public NettyMSocketFrameDecoder() {
    this(FRAME_LENGTH_MASK);
  }

  public NettyMSocketFrameDecoder(int maxFrameLength) {
    super(maxFrameLength, 0, FRAME_LENGTH_SIZE, 0, 0);
  }

  public Object decode(ByteBuf in) throws Exception {
    return decode(null, in);
  }
}
