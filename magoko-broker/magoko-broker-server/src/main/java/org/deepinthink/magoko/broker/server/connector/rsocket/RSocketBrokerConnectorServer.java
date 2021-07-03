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
package org.deepinthink.magoko.broker.server.connector.rsocket;

import io.rsocket.transport.netty.server.CloseableChannel;
import java.time.Duration;
import java.util.Objects;
import org.deepinthink.magoko.broker.server.connector.BrokerConnectorServer;
import reactor.core.publisher.Mono;

final class RSocketBrokerConnectorServer implements BrokerConnectorServer {

  private final Mono<CloseableChannel> starter;
  private Duration lifecycleTimeout;
  private CloseableChannel server;

  RSocketBrokerConnectorServer(Mono<CloseableChannel> starter, Duration lifecycleTimeout) {
    this.starter = Objects.requireNonNull(starter);
    this.lifecycleTimeout = lifecycleTimeout;
  }

  @Override
  public void start() {
    this.server = this.block(this.starter, this.lifecycleTimeout);
  }

  @Override
  public void stop() {
    if (!this.isRunning()) {
      this.server.dispose();
      this.server = null;
    }
  }

  @Override
  public boolean isRunning() {
    return Objects.nonNull(this.server) && !this.server.isDisposed();
  }

  private <T> T block(Mono<T> mono, Duration lifecycleTimeout) {
    return Objects.isNull(lifecycleTimeout) ? mono.block() : mono.block(lifecycleTimeout);
  }
}
