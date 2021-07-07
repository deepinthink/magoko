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

import io.rsocket.RSocket;
import io.rsocket.exceptions.ConnectionErrorException;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;
import org.deepinthink.magoko.broker.client.BrokerServerTarget;
import org.deepinthink.magoko.broker.client.rsocket.loadbalance.BrokerClientRSocketPool;
import org.springframework.context.SmartLifecycle;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

final class BrokerClientRSocketRequesterBootstrap implements SmartLifecycle {

  private final BrokerClientRSocketPool rSocketPool;
  private final BrokerServerTarget serverTarget;
  private final RSocketRequester.Builder builder;

  private RSocketRequester rSocketRequester;

  private static final Predicate<? super Throwable> CONNECTION_ERROR_PREDICATE =
      (e) ->
          e instanceof ClosedChannelException
              || e instanceof ConnectionErrorException
              || e instanceof ConnectException;

  BrokerClientRSocketRequesterBootstrap(
      RSocketRequester.Builder builder,
      BrokerClientRSocketPool rSocketPool,
      BrokerServerTarget serverTarget) {
    this.builder = builder;
    this.rSocketPool = rSocketPool;
    this.serverTarget = serverTarget;
  }

  @Override
  public void start() {
    this.tryToConnect()
        .doOnError(
            (e) ->
                Mono.just(e)
                    .filter(CONNECTION_ERROR_PREDICATE) // matching socket connection exception
                    .delayElement(Duration.ofSeconds(5)) // fixed reconnect delay (5 seconds)
                    .doFinally((__) -> this.start())
                    .subscribe())
        .subscribe(
            (rSocket) ->
                Mono.fromSupplier(this.serverTarget::getKey)
                    .doOnNext((key) -> this.rSocketPool.addRSocket(key, rSocket))
                    .then(rSocket.onClose()) // remove rsocket from pool when connection closed
                    .doOnSuccess((__) -> this.rSocketPool.removeRSocket(this.serverTarget.getKey()))
                    .doFinally((__) -> this.start())
                    .subscribe());
  }

  private Mono<RSocket> tryToConnect() {
    this.rSocketRequester = // always create new requester
        this.builder.tcp(this.serverTarget.getHost(), this.serverTarget.getPort());
    return this.rSocketRequester.rsocketClient().source();
  }

  @Override
  public void stop() {
    Mono.just(this.rSocketRequester)
        .filter(Objects::nonNull)
        .doOnSuccess(RSocketRequester::dispose)
        .subscribe();
  }

  @Override
  public boolean isRunning() {
    return true; // always be true
  }
}
