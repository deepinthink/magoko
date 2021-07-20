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
package org.deepinthink.magoko.broker.core.routing.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.Map;
import org.deepinthink.magoko.broker.core.routing.*;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

public class RSocketRoutingFrameEncoder extends AbstractEncoder<RSocketRoutingFrame> {

  public RSocketRoutingFrameEncoder() {
    super(RSocketRoutingMimeTypes.ROUTING_FRAME_MIME_TYPE);
  }

  @Override
  public Flux<DataBuffer> encode(
      Publisher<? extends RSocketRoutingFrame> inputStream,
      DataBufferFactory bufferFactory,
      ResolvableType elementType,
      MimeType mimeType,
      Map<String, Object> hints) {
    return Flux.from(inputStream)
        .map((value) -> encodeValue(value, bufferFactory, elementType, mimeType, hints));
  }

  @Override
  public DataBuffer encodeValue(
      RSocketRoutingFrame routingFrame,
      DataBufferFactory bufferFactory,
      ResolvableType valueType,
      MimeType mimeType,
      Map<String, Object> hints) {
    NettyDataBufferFactory factory = (NettyDataBufferFactory) bufferFactory;
    ByteBufAllocator allocator = factory.getByteBufAllocator();
    RSocketRoutingFrameType frameType = routingFrame.getFrameType();
    ByteBuf encoded;
    switch (frameType) {
      case ROUTE_SETUP:
        RSocketRoutingRouteSetup routeSetup = (RSocketRoutingRouteSetup) routingFrame;
        encoded =
            RSocketRoutingRouteSetupCodec.encode(
                allocator, routeSetup.getRouteId(), routingFrame.getFlags());
        break;
      case ADDRESS:
        RSocketRoutingAddress address = (RSocketRoutingAddress) routingFrame;
        encoded =
            RSocketRoutingAddressCodec.encode(
                allocator, address.getOriginRouteId(), routingFrame.getFlags());
        break;
      default:
        throw new IllegalArgumentException("Unknown rsocket routing frame type " + frameType);
    }
    return factory.wrap(encoded);
  }
}
