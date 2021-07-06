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

import org.deepinthink.magoko.broker.client.BrokerServerTarget;
import org.deepinthink.magoko.broker.client.rsocket.loadbalance.BrokerClientRSocketPool;
import org.springframework.context.SmartLifecycle;
import org.springframework.messaging.rsocket.RSocketRequester;

final class BrokerClientRSocketRequesterBootstrap implements SmartLifecycle {

  private final BrokerClientRSocketPool rSocketPool;
  private final BrokerServerTarget serverTarget;
  private final RSocketRequester rSocketRequester;

  BrokerClientRSocketRequesterBootstrap(
      RSocketRequester.Builder builder,
      BrokerClientRSocketPool rSocketPool,
      BrokerServerTarget serverTarget) {
    this.rSocketPool = rSocketPool;
    this.serverTarget = serverTarget;
    this.rSocketRequester = builder.tcp(this.serverTarget.getHost(), this.serverTarget.getPort());
  }

  @Override
  public void start() {
    this.rSocketRequester
        .rsocketClient()
        .source()
        .doOnTerminate(
            () -> {
              this.rSocketPool.removeRSocket(this.serverTarget.getKey());
              this.start();
            })
        .doOnSuccess((rSocket) -> this.rSocketPool.addRSocket(this.serverTarget.getKey(), rSocket))
        .subscribe();
  }

  @Override
  public void stop() {
    this.rSocketRequester.dispose();
  }

  @Override
  public boolean isRunning() {
    return !this.rSocketRequester.isDisposed();
  }
}
