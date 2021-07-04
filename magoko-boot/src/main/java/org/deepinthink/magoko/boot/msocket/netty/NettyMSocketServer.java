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
package org.deepinthink.magoko.boot.msocket.netty;

import static org.deepinthink.magoko.boot.msocket.MSocketConstants.DEFAULT_SERVER_DAEMON_AWAIT_THREAD_NAME;

import java.time.Duration;
import java.util.Objects;
import org.deepinthink.magoko.boot.msocket.server.MSocketServer;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;

final class NettyMSocketServer implements MSocketServer {

  private final Mono<? extends DisposableServer> serverStarter;
  private Duration lifecycleTimeout;
  private DisposableServer server;

  NettyMSocketServer(Mono<? extends DisposableServer> serverStarter, Duration lifecycleTimeout) {
    this.serverStarter = Objects.requireNonNull(serverStarter);
    this.lifecycleTimeout = lifecycleTimeout;
  }

  @Override
  public void start() {
    this.server = this.block(this.serverStarter, this.lifecycleTimeout);
    this.startDaemonAwaitThread(this.server);
  }

  private void startDaemonAwaitThread(DisposableServer server) {
    Thread thread =
        new Thread(() -> server.onDispose().block(), DEFAULT_SERVER_DAEMON_AWAIT_THREAD_NAME);
    thread.setContextClassLoader(getClass().getClassLoader());
    thread.setDaemon(false);
    thread.start();
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

  private <T> T block(Mono<T> mono, Duration timeout) {
    return Objects.nonNull(timeout) ? mono.block(timeout) : mono.block();
  }
}
