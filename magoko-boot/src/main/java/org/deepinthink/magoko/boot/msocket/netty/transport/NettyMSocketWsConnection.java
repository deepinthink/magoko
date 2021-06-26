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
package org.deepinthink.magoko.boot.msocket.netty.transport;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;

final class NettyMSocketWsConnection extends AbstractNettyMSocketConnection {
  NettyMSocketWsConnection(Connection connection) {
    super(connection);
    this.connection
        .outbound()
        .sendObject(this.sender.asFlux().map(BinaryWebSocketFrame::new))
        .then()
        .subscribe();
  }

  @Override
  public Flux<ByteBuf> receiveFrames() {
    return this.connection.inbound().receive();
  }
}
