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

import static org.deepinthink.magoko.boot.msocket.netty.codec.NettyMSocketFrameLengthCodec.FRAME_LENGTH_MASK;

import java.util.Objects;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketConnectionAcceptor;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketTcpConnection;
import org.deepinthink.magoko.boot.msocket.netty.codec.NettyMSocketFrameDecoder;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

public final class NettyMSocketTcpServerTransport implements NettyMSocketServerTransport {
  private final TcpServer tcpServer;

  NettyMSocketTcpServerTransport(TcpServer tcpServer) {
    this.tcpServer = Objects.requireNonNull(tcpServer);
  }

  public static NettyMSocketTcpServerTransport create(TcpServer tcpServer) {
    return new NettyMSocketTcpServerTransport(tcpServer);
  }

  @Override
  public Mono<? extends DisposableServer> bind(NettyMSocketConnectionAcceptor acceptor) {
    return this.tcpServer
        .doOnConnection(
            connection -> {
              connection.addHandlerLast(new NettyMSocketFrameDecoder(FRAME_LENGTH_MASK));
              acceptor
                  .apply(new NettyMSocketTcpConnection(connection))
                  .then(Mono.<Void>never())
                  .subscribe(connection.disposeSubscriber());
            })
        .bind();
  }
}
