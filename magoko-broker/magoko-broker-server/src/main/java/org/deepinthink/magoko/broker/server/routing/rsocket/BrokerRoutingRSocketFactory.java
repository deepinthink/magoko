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
package org.deepinthink.magoko.broker.server.routing.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import java.util.Objects;
import java.util.function.Function;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingAddress;
import org.deepinthink.magoko.broker.server.core.rsocket.BrokerRSocketLocator;

public class BrokerRoutingRSocketFactory {

  private final BrokerRSocketLocator rSocketLocator;
  private final Function<Payload, RSocketRoutingAddress> addressExtractor;

  public BrokerRoutingRSocketFactory(
      BrokerRSocketLocator rSocketLocator,
      Function<Payload, RSocketRoutingAddress> addressExtractor) {
    this.rSocketLocator = Objects.requireNonNull(rSocketLocator);
    this.addressExtractor = Objects.requireNonNull(addressExtractor);
  }

  public RSocket create() {
    return new BrokerRoutingRSocket(this.rSocketLocator, this.addressExtractor);
  }
}
