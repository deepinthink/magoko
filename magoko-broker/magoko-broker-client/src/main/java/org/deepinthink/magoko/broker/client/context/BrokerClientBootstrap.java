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
package org.deepinthink.magoko.broker.client.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.deepinthink.magoko.broker.client.BrokerServerTarget;
import org.deepinthink.magoko.broker.client.rsocket.loadbalance.BrokerClientRSocketPool;
import org.springframework.context.SmartLifecycle;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

public final class BrokerClientBootstrap implements SmartLifecycle {

  private final Map<String, BrokerClientRSocketRequesterBootstrap> requesterBootstrapMap =
      new HashMap<>();

  private volatile boolean running = false;

  public BrokerClientBootstrap(
      RSocketRequester.Builder builder,
      BrokerClientRSocketPool rSocketPool,
      List<BrokerServerTarget> serverTargets) {
    Assert.notEmpty(serverTargets, "broker server targets should be empty or null!");
    serverTargets.forEach(
        (target) -> {
          BrokerClientRSocketRequesterBootstrap requesterBootstrap =
              new BrokerClientRSocketRequesterBootstrap(builder, rSocketPool, target);
          this.requesterBootstrapMap.put(target.getKey(), requesterBootstrap);
        });
  }

  public Collection<BrokerClientRSocketRequesterBootstrap> getRequesterBootstraps() {
    return this.requesterBootstrapMap.values();
  }

  @Override
  public void start() {
    Flux.fromIterable(this.requesterBootstrapMap.values())
        .doOnNext(BrokerClientRSocketRequesterBootstrap::start)
        .doFinally((__) -> this.running = true)
        .subscribe();
  }

  @Override
  public void stop() {
    Flux.fromIterable(this.requesterBootstrapMap.values())
        .doOnNext(BrokerClientRSocketRequesterBootstrap::stop)
        .doFinally((__) -> this.running = false)
        .subscribe();
  }

  @Override
  public boolean isRunning() {
    return this.running;
  }
}
