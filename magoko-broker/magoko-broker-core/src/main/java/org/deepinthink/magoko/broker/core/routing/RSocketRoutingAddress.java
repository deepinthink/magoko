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
package org.deepinthink.magoko.broker.core.routing;

import io.netty.buffer.ByteBuf;
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.codec.RSocketRoutingAddressCodec;

public class RSocketRoutingAddress extends RSocketRoutingFrame {

  private final int routeId;

  public static RSocketRoutingAddress from(ByteBuf byteBuf, int flags) {
    return new Builder().flags(flags).routeId(byteBuf.readInt()).build();
  }

  private RSocketRoutingAddress(int flags, int routeId) {
    super(RSocketRoutingFrameType.ADDRESS, flags);
    this.routeId = routeId;
  }

  public RSocketRoutingType getRoutingType() {
    int routingType = getFlags() & (~RSocketRoutingAddressCodec.ROUTING_TYPE_MASK);
    return RSocketRoutingType.from(routingType);
  }

  public int getRouteId() {
    return routeId;
  }

  public static class Builder {
    private int flags = RSocketRoutingAddressCodec.FLAGS_U; // default UNICAST
    private int routeId;

    private Builder() {}

    public Builder flags(int flags) {
      this.flags = flags;
      return this;
    }

    public Builder routeId(int routeId) {
      this.routeId = routeId;
      return this;
    }

    public Builder routingType(RSocketRoutingType routingType) {
      if (Objects.nonNull(routingType)) {
        flags &= RSocketRoutingAddressCodec.ROUTING_TYPE_MASK;
        flags |= routingType.getFlag();
      }
      return this;
    }

    public RSocketRoutingAddress build() {
      return new RSocketRoutingAddress(this.flags, this.routeId);
    }
  }
}
