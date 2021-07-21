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

import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.codec.RSocketRoutingAddressCodec;

public class RSocketRoutingAddressBuilder
    extends RSocketRoutingTagsBuilder<RSocketRoutingAddressBuilder> {

  // TODO: 2021/7/21 add metadata tags?
  private final RSocketRoutingRouteId originRouteId;
  private int flags = RSocketRoutingAddressCodec.FLAGS_U; // default UNICAST

  RSocketRoutingAddressBuilder(RSocketRoutingRouteId originRouteId) {
    this.originRouteId = Objects.requireNonNull(originRouteId);
  }

  public RSocketRoutingAddressBuilder flags(int flags) {
    this.flags = flags;
    return this;
  }

  public RSocketRoutingAddressBuilder routingType(RSocketRoutingType routingType) {
    if (Objects.nonNull(routingType)) {
      flags &= RSocketRoutingAddressCodec.ROUTING_TYPE_MASK;
      flags |= routingType.getFlag();
    }
    return this;
  }

  public RSocketRoutingAddress build() {
    RSocketRoutingTags tags = buildTags();
    if (tags.isEmpty()) {
      throw new IllegalArgumentException("RSocket routing address tags CAN NOT be null");
    }
    return new RSocketRoutingAddress(this.originRouteId, tags, this.flags);
  }
}
