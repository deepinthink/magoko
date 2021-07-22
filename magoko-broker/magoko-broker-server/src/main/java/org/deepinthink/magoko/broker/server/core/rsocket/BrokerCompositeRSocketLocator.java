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
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingType;

public class BrokerCompositeRSocketLocator implements BrokerRSocketLocator {

  private final List<BrokerRSocketLocator> locators; // locator store

  public BrokerCompositeRSocketLocator(List<BrokerRSocketLocator> locators) {
    this.locators = Objects.requireNonNull(locators);
  }

  @Override
  public boolean supports(RSocketRoutingType routingType) {
    for (BrokerRSocketLocator locator : this.locators) {
      if (locator.supports(routingType)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public RSocket locate(RSocketRoutingAddress address) {
    for (BrokerRSocketLocator locator : this.locators) {
      if (locator.supports(address.getRoutingType())) {
        return locator.locate(address);
      }
    }
    throw new IllegalStateException(
        "CAN NOT find BrokerRSocketLocator for routing type " + address.getRoutingType());
  }
}
