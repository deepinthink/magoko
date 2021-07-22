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

import io.rsocket.Payload;
import io.rsocket.RSocket;
import java.util.Objects;
import java.util.function.Function;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingAddress;
import org.deepinthink.magoko.broker.server.core.rsocket.BrokerRSocketLocator;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BrokerRoutingRSocket implements RSocket {

  private final BrokerRSocketLocator rSocketLocator;
  private final Function<Payload, RSocketRoutingAddress> addressExtractor;

  BrokerRoutingRSocket(
      BrokerRSocketLocator rSocketLocator,
      Function<Payload, RSocketRoutingAddress> addressExtractor) {
    this.rSocketLocator = rSocketLocator;
    this.addressExtractor = addressExtractor;
  }

  @Override
  public Mono<Void> fireAndForget(Payload payload) {
    try {
      RSocket rSocket = locate(payload);
      return rSocket.fireAndForget(payload);
    } catch (Throwable cause) {
      payload.release();
      // TODO: 2021/7/22 error handle
      return Mono.error(cause);
    }
  }

  @Override
  public Mono<Payload> requestResponse(Payload payload) {
    try {
      RSocket rSocket = locate(payload);
      return rSocket.requestResponse(payload);
    } catch (Throwable cause) {
      payload.release();
      return Mono.error(cause);
    }
  }

  @Override
  public Flux<Payload> requestStream(Payload payload) {
    try {
      RSocket rSocket = locate(payload);
      return rSocket.requestStream(payload);
    } catch (Throwable cause) {
      payload.release();
      return Flux.error(cause);
    }
  }

  @Override
  public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
    return Flux.from(payloads)
        .switchOnFirst(
            (first, flux) -> {
              if (first.hasValue()) {
                Payload payload = Objects.requireNonNull(first.get());
                try {
                  RSocket rSocket = locate(payload); // first payload with metadata
                  return rSocket.requestChannel(flux);
                } catch (Throwable cause) {
                  payload.release();
                  return Flux.error(cause);
                }
              }
              return flux;
            });
  }

  @Override
  public Mono<Void> metadataPush(Payload payload) {
    try {
      RSocket rSocket = locate(payload);
      return rSocket.metadataPush(payload);
    } catch (Throwable cause) {
      payload.release();
      return Mono.error(cause);
    }
  }

  private RSocket locate(Payload payload) {
    RSocketRoutingAddress address = this.addressExtractor.apply(payload);
    if (!this.rSocketLocator.supports(address.getRoutingType())) {
      throw new IllegalStateException(
          "NO BrokerRSocketLocator for routing type " + address.getRoutingType());
    }
    return this.rSocketLocator.locate(address);
  }
}
