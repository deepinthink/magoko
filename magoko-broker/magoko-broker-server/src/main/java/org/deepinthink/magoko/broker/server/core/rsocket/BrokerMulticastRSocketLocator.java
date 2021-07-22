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
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingAddress;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingType;
import org.deepinthink.magoko.broker.server.core.query.BrokerRSocketQuery;

public class BrokerMulticastRSocketLocator implements BrokerRSocketLocator {

  private final BrokerRSocketQuery rSocketQuery;

  public BrokerMulticastRSocketLocator(BrokerRSocketQuery rSocketQuery) {
    this.rSocketQuery = Objects.requireNonNull(rSocketQuery);
  }

  @Override
  public boolean supports(RSocketRoutingType routingType) {
    return routingType == RSocketRoutingType.MULTICAST;
  }

  @Override
  public RSocket locate(RSocketRoutingAddress address) { // local through query with tags
    return new BrokerMulticastRSocket(() -> rSocketQuery.query(address.getTags()));
  }
}
