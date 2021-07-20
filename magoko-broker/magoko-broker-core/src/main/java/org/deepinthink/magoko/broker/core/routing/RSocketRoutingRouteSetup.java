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

import static org.deepinthink.magoko.broker.core.routing.codec.RSocketRoutingRouteSetupCodec.routeId;

import io.netty.buffer.ByteBuf;
import java.util.Objects;

public final class RSocketRoutingRouteSetup extends RSocketRoutingFrame {

  private final RSocketRoutingRouteId routeId;

  public static Builder from(RSocketRoutingRouteId routeId) {
    return new Builder(routeId);
  }

  public static RSocketRoutingRouteSetup from(ByteBuf byteBuf) {
    return from(routeId(byteBuf)).build();
  }

  private RSocketRoutingRouteSetup(RSocketRoutingRouteId routeId) {
    super(RSocketRoutingFrameType.ROUTE_SETUP, 0);
    this.routeId = routeId;
  }

  public RSocketRoutingRouteId getRouteId() {
    return routeId;
  }

  public static final class Builder {
    private final RSocketRoutingRouteId routeId;

    private Builder(RSocketRoutingRouteId routeId) {
      this.routeId = Objects.requireNonNull(routeId);
    }

    public RSocketRoutingRouteSetup build() {
      return new RSocketRoutingRouteSetup(this.routeId);
    }
  }
}
