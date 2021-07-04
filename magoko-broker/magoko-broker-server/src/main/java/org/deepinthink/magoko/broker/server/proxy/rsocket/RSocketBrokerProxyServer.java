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
package org.deepinthink.magoko.broker.server.proxy.rsocket;

import static org.deepinthink.magoko.broker.server.BrokerServerConstants.RSOCKET_PROXY_SERVER_DAEMON_AWAIT_THREAD_NAME;

import io.rsocket.transport.netty.server.CloseableChannel;
import java.time.Duration;
import java.util.Objects;
import org.deepinthink.magoko.broker.server.proxy.BrokerProxyServer;
import reactor.core.publisher.Mono;

final class RSocketBrokerProxyServer implements BrokerProxyServer {

  private final Mono<CloseableChannel> serverStarter;
  private final Duration lifecycleTimeout;
  private CloseableChannel serverChannel;

  RSocketBrokerProxyServer(Mono<CloseableChannel> serverStarter, Duration lifecycleTimeout) {
    this.serverStarter = Objects.requireNonNull(serverStarter);
    this.lifecycleTimeout = lifecycleTimeout;
  }

  @Override
  public void start() {
    this.serverChannel = this.block(this.serverStarter, this.lifecycleTimeout);
    this.startDaemonAwaitThread(this.serverChannel);
  }

  private void startDaemonAwaitThread(CloseableChannel serverChannel) {
    Thread thread =
        new Thread(
            () -> serverChannel.onClose().block(), RSOCKET_PROXY_SERVER_DAEMON_AWAIT_THREAD_NAME);
    thread.setContextClassLoader(getClass().getClassLoader());
    thread.setDaemon(false);
    thread.start();
  }

  @Override
  public void stop() {
    if (!this.isRunning()) {
      this.serverChannel.dispose();
      this.serverChannel = null;
    }
  }

  @Override
  public boolean isRunning() {
    return Objects.nonNull(this.serverChannel) && !this.serverChannel.isDisposed();
  }

  private <T> T block(Mono<T> mono, Duration timeout) {
    return Objects.nonNull(timeout) ? mono.block(timeout) : mono.block();
  }
}
