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

import static org.deepinthink.magoko.broker.core.routing.codec.RSocketRoutingCodecUtils.decodeRouteId;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingFrameType;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingRouteId;

public final class RSocketRoutingRouteSetupCodec {

  public static ByteBuf encode(
      ByteBufAllocator allocator, RSocketRoutingRouteId routeId, int flags) {
    Objects.requireNonNull(routeId);
    ByteBuf byteBuf =
        RSocketRoutingFrameHeaderCodec.encode(
            allocator, RSocketRoutingFrameType.ROUTE_SETUP, flags);
    RSocketRoutingCodecUtils.encodeRouteId(byteBuf, routeId);
    return byteBuf;
  }

  public static RSocketRoutingRouteId routeId(ByteBuf byteBuf) {
    return decodeRouteId(byteBuf, RSocketRoutingFrameHeaderCodec.BYTES);
  }

  private RSocketRoutingRouteSetupCodec() {}
}
