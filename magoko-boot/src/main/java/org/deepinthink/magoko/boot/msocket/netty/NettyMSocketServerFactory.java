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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.Objects;
import org.deepinthink.magoko.boot.msocket.MSocketTransportType;
import org.deepinthink.magoko.boot.msocket.netty.transport.NettyMSocketServerTransport;
import org.deepinthink.magoko.boot.msocket.netty.transport.NettyMSocketTcpServerTransport;
import org.deepinthink.magoko.boot.msocket.netty.transport.NettyMSocketWebSocketServerTransport;
import org.deepinthink.magoko.boot.msocket.server.ConfigurableMSocketServerFactory;
import org.deepinthink.magoko.boot.msocket.server.MSocketServer;
import org.deepinthink.magoko.boot.msocket.server.MSocketServerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

public class NettyMSocketServerFactory
    implements MSocketServerFactory, ConfigurableMSocketServerFactory {

  private final NettyMSocketConnectionAcceptor connectionAcceptor;
  private InetAddress host;
  private int port;
  private MSocketTransportType transportType;
  private String mappingPath;
  private Duration lifecycleTimeout;

  public NettyMSocketServerFactory(NettyMSocketConnectionAcceptor connectionAcceptor) {
    this.connectionAcceptor = Objects.requireNonNull(connectionAcceptor);
  }

  @Override
  public MSocketServer createServer() {
    NettyMSocketServerTransport serverTransport =
        (this.transportType == MSocketTransportType.TCP)
            ? createTcpServerTransport()
            : createWebSocketServerTransport();
    Mono<? extends DisposableServer> serverStarter = serverTransport.bind(this.connectionAcceptor);
    return new NettyMSocketServer(serverStarter, this.lifecycleTimeout);
  }

  private NettyMSocketServerTransport createTcpServerTransport() {
    TcpServer tcpServer = TcpServer.create().bindAddress(this::getListenAddress);
    return NettyMSocketTcpServerTransport.create(tcpServer);
  }

  private NettyMSocketServerTransport createWebSocketServerTransport() {
    HttpServer httpServer = HttpServer.create().bindAddress(this::getListenAddress);
    return NettyMSocketWebSocketServerTransport.create(httpServer, this.mappingPath);
  }

  private SocketAddress getListenAddress() {
    return Objects.nonNull(this.host)
        ? new InetSocketAddress(this.host, this.port)
        : new InetSocketAddress(this.port);
  }

  @Override
  public void setHost(InetAddress host) {
    this.host = host;
  }

  @Override
  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public void setTransportType(MSocketTransportType transportType) {
    this.transportType = transportType;
  }

  @Override
  public void setMappingPath(String mappingPath) {
    this.mappingPath = mappingPath;
  }

  @Override
  public void setLifecycleTimeout(Duration lifecycleTimeout) {
    this.lifecycleTimeout = lifecycleTimeout;
  }
}
