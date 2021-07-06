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
package org.deepinthink.magoko.broker.client.rsocket.loadbalance;

import io.rsocket.RSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerClientRSocketPool implements BrokerClientRSocketSelector {

  private final BrokerClientLoadbalanceStrategy loadbalanceStrategy;
  private final Map<String, RSocket> rSocketMap = new ConcurrentHashMap<>();
  private static final NoAvailableRSocket NO_AVAILABLE_RSOCKET = new NoAvailableRSocket();

  public static BrokerClientRSocketPool create(
      BrokerClientLoadbalanceStrategy loadbalanceStrategy) {
    return new BrokerClientRSocketPool(loadbalanceStrategy);
  }

  BrokerClientRSocketPool(BrokerClientLoadbalanceStrategy loadbalanceStrategy) {
    this.loadbalanceStrategy = Objects.requireNonNull(loadbalanceStrategy);
  }

  @Override
  public RSocket select() {
    List<RSocket> rSocketList = new ArrayList<>(this.rSocketMap.values());
    return rSocketList.isEmpty()
        ? this.loadbalanceStrategy.select(rSocketList)
        : NO_AVAILABLE_RSOCKET;
  }

  public void addRSocket(String key, RSocket rSocket) {
    this.rSocketMap.put(key, rSocket);
  }

  public void removeRSocket(String key) {
    this.rSocketMap.remove(key);
  }

  static class NoAvailableRSocket implements RSocket {}
}
