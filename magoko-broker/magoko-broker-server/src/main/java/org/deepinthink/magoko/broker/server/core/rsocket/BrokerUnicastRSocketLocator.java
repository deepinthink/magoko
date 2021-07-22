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
package org.deepinthink.magoko.broker.server.core.rsocket;

import io.rsocket.RSocket;
import java.util.List;
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingAddress;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingTags;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingType;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingWellKnownKey;
import org.deepinthink.magoko.broker.server.core.query.BrokerRSocketQuery;

public class BrokerUnicastRSocketLocator implements BrokerRSocketLocator {

  private final BrokerRSocketQuery rSocketQuery;

  public BrokerUnicastRSocketLocator(BrokerRSocketQuery rSocketQuery) {
    this.rSocketQuery = Objects.requireNonNull(rSocketQuery);
  }

  @Override
  public boolean supports(RSocketRoutingType routingType) {
    return routingType == RSocketRoutingType.UNICAST;
  }

  @Override
  public RSocket locate(RSocketRoutingAddress address) {
    List<RSocket> rSockets = rSocketQuery.query(address.getTags());
    final int size = rSockets.size();
    switch (size) {
      case 0:
        return null; // FIXME: 2021/7/22
      case 1:
        return rSockets.get(0);
      default:
        return loadbalance(rSockets, address.getTags());
    }
  }

  private RSocket loadbalance(List<RSocket> rSockets, RSocketRoutingTags tags) {
    if (tags.containsKey(RSocketRoutingWellKnownKey.LB_METHOD)) { // check LB_METHOD
      String lbMethod = tags.get(RSocketRoutingWellKnownKey.LB_METHOD);
      // TODO: 2021/7/22 get loadbalance strategy with given lb method
    }
    return null;
  }
}
