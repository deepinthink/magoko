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

import io.rsocket.RSocket;
import java.util.Collection;
import java.util.List;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingRouteId;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingTags;
import org.deepinthink.magoko.broker.server.util.IndexedMap;
import org.deepinthink.magoko.broker.server.util.RoaringBitmapIndexedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerRSocketRoutingIndex
    implements IndexedMap<RSocketRoutingRouteId, RSocket, RSocketRoutingTags> {

  private static final Logger logger = LoggerFactory.getLogger(BrokerRSocketRoutingIndex.class);

  private final IndexedMap<RSocketRoutingRouteId, RSocket, RSocketRoutingTags> indexedMap =
      new RoaringBitmapIndexedMap<>();

  @Override
  public RSocket get(RSocketRoutingRouteId routeId) {
    return this.indexedMap.get(routeId);
  }

  @Override
  public RSocket put(RSocketRoutingRouteId routeId, RSocket value, RSocketRoutingTags tags) {
    if (logger.isDebugEnabled()) {
      logger.debug("Indexing RSocket for RouteId {} tags {}", routeId, tags);
    }
    return this.indexedMap.put(routeId, value, tags);
  }

  @Override
  public RSocket remove(RSocketRoutingRouteId routeId) {
    if (logger.isDebugEnabled()) {
      logger.debug("Removing RSocket for RouteId {}", routeId);
    }
    return this.indexedMap.remove(routeId);
  }

  @Override
  public int size() {
    return this.indexedMap.size();
  }

  @Override
  public boolean isEmpty() {
    return this.indexedMap.isEmpty();
  }

  @Override
  public void clear() {
    this.indexedMap.clear();
  }

  @Override
  public Collection<RSocket> values() {
    return this.indexedMap.values();
  }

  @Override
  public List<RSocket> query(RSocketRoutingTags tags) {
    List<RSocket> rSockets = this.indexedMap.query(tags);
    if (logger.isDebugEnabled()) {
      logger.debug("Found RSocket for Routing Tags {} results {}", tags, rSockets);
    }
    return rSockets;
  }
}
