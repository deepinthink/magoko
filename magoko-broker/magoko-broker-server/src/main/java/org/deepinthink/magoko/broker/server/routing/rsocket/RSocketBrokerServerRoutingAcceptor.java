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

import static org.deepinthink.magoko.broker.core.routing.RSocketRoutingMimeTypes.ROUTING_FRAME_MIME_TYPE;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import java.util.Map;
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingFrame;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingRouteSetup;
import org.deepinthink.magoko.broker.server.routing.BrokerServerRoutingAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RSocketBrokerServerRoutingAcceptor implements BrokerServerRoutingAcceptor {
  private static final Logger logger =
      LoggerFactory.getLogger(RSocketBrokerServerRoutingAcceptor.class);

  private final BrokerRSocketRoutingIndex routingIndex;
  private final BrokerRoutingRSocketFactory routingRSocketFactory;
  private final MetadataExtractor metadataExtractor;

  public RSocketBrokerServerRoutingAcceptor(
      BrokerRSocketRoutingIndex routingIndex,
      BrokerRoutingRSocketFactory routingRSocketFactory,
      MetadataExtractor metadataExtractor) {
    this.routingIndex = Objects.requireNonNull(routingIndex);
    this.routingRSocketFactory = Objects.requireNonNull(routingRSocketFactory);
    this.metadataExtractor = Objects.requireNonNull(metadataExtractor);
  }

  @Override
  public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket sendingSocket) {
    try {
      MimeType mimeType = MimeType.valueOf(setupPayload.metadataMimeType());
      Map<String, Object> setupMetadataMap = // result map always be exists
          this.metadataExtractor.extract(setupPayload, mimeType);
      if (setupMetadataMap.containsKey(ROUTING_FRAME_MIME_TYPE)) {
        RSocketRoutingFrame routingFrame =
            (RSocketRoutingFrame) setupMetadataMap.get(ROUTING_FRAME_MIME_TYPE);
        Runnable doCleanup = () -> cleanup(routingFrame);
        RSocket wrapRSocket = wrapSendingRSocket(sendingSocket, routingFrame);
        if (routingFrame instanceof RSocketRoutingRouteSetup) { // RouteSetup frame required
          RSocketRoutingRouteSetup routeSetup = (RSocketRoutingRouteSetup) routingFrame;
          return Mono.defer(
              () -> {
                routingIndex.put(routeSetup.getRouteId(), wrapRSocket, routeSetup.getTags());
                return finalize(sendingSocket, doCleanup);
              });
        }
      }
      // todo: replace with pre-defined error code?
      throw new IllegalStateException("RSocketRoutingRouteSetup frame not found in metadata");
    } catch (Throwable cause) {
      logger.error("Accept rsocket RouteSetup failed", cause);
      return Mono.error(cause);
    }
  }

  private void cleanup(RSocketRoutingFrame routingFrame) {
    if (routingFrame instanceof RSocketRoutingRouteSetup) {
      RSocketRoutingRouteSetup setup = (RSocketRoutingRouteSetup) routingFrame;
      this.routingIndex.remove(setup.getRouteId());
    }
  }

  private RSocket wrapSendingRSocket(RSocket sendingSocket, RSocketRoutingFrame routingFrame) {
    // FIXME: 2021/7/22 DelegatingRSocket to handle error
    return sendingSocket;
  }

  private Mono<RSocket> finalize(RSocket sendingSocket, Runnable doCleanup) {
    RSocket receivingRSocket = this.routingRSocketFactory.create();
    Flux.firstWithSignal( // rsocket combo close
            receivingRSocket.onClose(),
            sendingSocket.onClose()) // any rsocket closed will emit signal
        .doFinally(__ -> doCleanup.run())
        .subscribe();
    return Mono.just(receivingRSocket);
  }
}
