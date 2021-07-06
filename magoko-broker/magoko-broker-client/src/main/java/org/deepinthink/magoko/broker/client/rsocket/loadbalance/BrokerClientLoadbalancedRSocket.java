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

import io.rsocket.Payload;
import io.rsocket.RSocket;
import java.util.Objects;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class BrokerClientLoadbalancedRSocket implements RSocket {

  private final BrokerClientRSocketSelector selector;

  public static BrokerClientLoadbalancedRSocket create(BrokerClientRSocketSelector selector) {
    return new BrokerClientLoadbalancedRSocket(selector);
  }

  BrokerClientLoadbalancedRSocket(BrokerClientRSocketSelector selector) {
    this.selector = Objects.requireNonNull(selector);
  }

  @Override
  public Mono<Void> fireAndForget(Payload payload) {
    return this.selector.select().fireAndForget(payload);
  }

  @Override
  public Mono<Payload> requestResponse(Payload payload) {
    return this.selector.select().requestResponse(payload);
  }

  @Override
  public Flux<Payload> requestStream(Payload payload) {
    return this.selector.select().requestStream(payload);
  }

  @Override
  public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
    return this.selector.select().requestChannel(payloads);
  }

  @Override
  public Mono<Void> metadataPush(Payload payload) {
    return this.selector.select().metadataPush(payload);
  }
}
