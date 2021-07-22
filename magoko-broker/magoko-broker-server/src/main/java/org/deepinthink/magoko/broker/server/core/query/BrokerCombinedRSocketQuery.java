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
package org.deepinthink.magoko.broker.server.core.query;

import io.netty.util.concurrent.FastThreadLocal;
import io.rsocket.RSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingTags;
import org.deepinthink.magoko.broker.server.routing.rsocket.BrokerRoutingRSocketIndex;

public class BrokerCombinedRSocketQuery implements BrokerRSocketQuery {

  private static final FastThreadLocal<List<RSocket>> RSOCKET_STORE;

  static {
    RSOCKET_STORE =
        new FastThreadLocal<List<RSocket>>() {
          @Override
          protected List<RSocket> initialValue() {
            return new ArrayList<>();
          }
        };
  }

  private final BrokerRoutingRSocketIndex routingIndex;

  public BrokerCombinedRSocketQuery(BrokerRoutingRSocketIndex routingIndex) {
    this.routingIndex = routingIndex;
  }

  @Override
  public List<RSocket> query(RSocketRoutingTags tags) {
    if (Objects.isNull(tags) || tags.isEmpty()) {
      return null;
    }

    List<RSocket> rSocketStored = RSOCKET_STORE.get();
    rSocketStored.clear();

    List<RSocket> rSockets = routingIndex.query(tags);
    if (Objects.nonNull(rSockets) && !rSockets.isEmpty()) {
      rSocketStored.addAll(rSockets);
    }

    return rSocketStored;
  }
}
