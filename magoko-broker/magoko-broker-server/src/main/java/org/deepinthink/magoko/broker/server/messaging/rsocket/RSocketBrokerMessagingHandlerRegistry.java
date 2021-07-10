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
package org.deepinthink.magoko.broker.server.messaging.rsocket;

import static org.deepinthink.magoko.broker.core.routing.RSocketRoutingMimeTypes.ROUTING_FRAME_MIME_TYPE;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import java.util.Map;
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingFrame;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingRouteSetup;
import org.deepinthink.magoko.broker.server.messaging.BrokerMessagingHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;

public class RSocketBrokerMessagingHandlerRegistry implements BrokerMessagingHandlerRegistry {
  private static final Logger logger =
      LoggerFactory.getLogger(RSocketBrokerMessagingHandlerRegistry.class);

  private final MetadataExtractor metadataExtractor;

  public RSocketBrokerMessagingHandlerRegistry(MetadataExtractor metadataExtractor) {
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
        if (routingFrame instanceof RSocketRoutingRouteSetup) { // RouteSetup frame required
          return Mono.just(sendingSocket);
        }
      }
      // todo: replace with pre-defined error code?
      throw new IllegalStateException("RSocketRoutingRouteSetup frame not found in metadata");
    } catch (Throwable cause) {
      logger.error("Accept rsocket RouteSetup failed", cause);
      return Mono.error(cause);
    }
  }
}
