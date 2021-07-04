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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketConnectionAcceptor;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketWebSocketConnection;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.WebsocketServerSpec;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

public class NettyMSocketWebSocketServerTransport implements NettyMSocketServerTransport {
  private final HttpServer httpServer;
  private final String mappingPath;
  private static final ChannelHandler pongHandler = new PongHandler();

  static Function<HttpServer, HttpServer> serverConfigurer =
      httpServer -> httpServer.doOnConnection(connection -> connection.addHandlerLast(pongHandler));

  final WebsocketServerSpec.Builder specBuilder =
      WebsocketServerSpec.builder().maxFramePayloadLength(FRAME_LENGTH_MASK);

  NettyMSocketWebSocketServerTransport(HttpServer httpServer, String mappingPath) {
    this.httpServer = serverConfigurer.apply(Objects.requireNonNull(httpServer));
    this.mappingPath = mappingPath;
  }

  public static NettyMSocketWebSocketServerTransport create(
      HttpServer httpServer, String mappingPath) {
    return new NettyMSocketWebSocketServerTransport(httpServer, mappingPath);
  }

  @Override
  public Mono<? extends DisposableServer> bind(NettyMSocketConnectionAcceptor acceptor) {
    return this.httpServer
        .route(routes -> routes.ws(this.mappingPath, newHandler(acceptor), specBuilder.build()))
        .bind();
  }

  public static BiFunction<WebsocketInbound, WebsocketOutbound, Publisher<Void>> newHandler(
      NettyMSocketConnectionAcceptor acceptor) {
    return (wsInbound, wsOutBound) ->
        acceptor
            .apply(new NettyMSocketWebSocketConnection((Connection) wsInbound))
            .then(wsOutBound.neverComplete());
  }

  @ChannelHandler.Sharable
  private static class PongHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      if (msg instanceof PongWebSocketFrame) {
        ReferenceCountUtil.safeRelease(msg);
        ctx.read();
      } else {
        ctx.fireChannelRead(msg);
      }
    }
  }
}
