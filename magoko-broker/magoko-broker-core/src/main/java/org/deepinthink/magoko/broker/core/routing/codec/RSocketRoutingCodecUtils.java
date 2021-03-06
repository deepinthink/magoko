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
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingRouteId;

public final class RSocketRoutingCodecUtils {

  public static final int ROUTE_ID_BITS = 16; // MSB(8bytes), LSB(8bytes)

  static RSocketRoutingRouteId decodeRouteId(ByteBuf byteBuf, int offset) {
    long msb = byteBuf.getLong(offset);
    long lsb = byteBuf.getLong(offset + Long.BYTES);
    return new RSocketRoutingRouteId(msb, lsb);
  }

  static void encodeRouteId(ByteBuf byteBuf, RSocketRoutingRouteId routeId) {
    byteBuf.writeLong(routeId.getMsb()).writeLong(routeId.getLsb());
  }

  private RSocketRoutingCodecUtils() {}
}
