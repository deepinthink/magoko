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
import java.time.Duration;
import java.util.Objects;
import org.deepinthink.magoko.boot.msocket.ConfigurableMSocketServerFactory;
import org.deepinthink.magoko.boot.msocket.MSocketServer;
import org.deepinthink.magoko.boot.msocket.MSocketServerFactory;
import org.deepinthink.magoko.boot.msocket.config.MSocketProperties.TransportType;
import org.deepinthink.magoko.boot.msocket.netty.transport.NettyMSocketConnectionAcceptor;
import org.deepinthink.magoko.boot.msocket.netty.transport.NettyMSocketTcpServerTransport;
import org.deepinthink.magoko.boot.msocket.netty.transport.NettyMSocketWsServerTransport;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

public class NettyMSocketServerFactory
    implements MSocketServerFactory, ConfigurableMSocketServerFactory {

  private final NettyMSocketConnectionAcceptor connectionAcceptor;
  private InetAddress host;
  private int port;
  private String mappingPath;
  private TransportType transportType;
  private Duration lifecycleTimeout;

  public NettyMSocketServerFactory(NettyMSocketConnectionAcceptor connectionAcceptor) {
    this.connectionAcceptor = Objects.requireNonNull(connectionAcceptor);
  }

  @Override
  public MSocketServer createServer() {
    return (this.transportType == TransportType.TCP)
        ? this.createTcpServer()
        : this.createWsServer();
  }

  private MSocketServer createTcpServer() {
    TcpServer tcpServer = TcpServer.create().bindAddress(this::getListenAddress);
    NettyMSocketTcpServerTransport tcpServerTransport =
        NettyMSocketTcpServerTransport.create(tcpServer);
    return new NettyMSocketServer(
        tcpServerTransport.bind(this.connectionAcceptor), this.lifecycleTimeout);
  }

  private MSocketServer createWsServer() {
    HttpServer httpServer = HttpServer.create().bindAddress(this::getListenAddress);
    NettyMSocketWsServerTransport wsServerTransport =
        NettyMSocketWsServerTransport.create(httpServer, this.mappingPath);
    return new NettyMSocketServer(
        wsServerTransport.bind(this.connectionAcceptor), this.lifecycleTimeout);
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
  public void setMappingPath(String mappingPath) {
    this.mappingPath = mappingPath;
  }

  @Override
  public void setTransportType(TransportType transportType) {
    this.transportType = transportType;
  }

  @Override
  public void setLifecycleTimeout(Duration lifecycleTimeout) {
    this.lifecycleTimeout = lifecycleTimeout;
  }

  private InetSocketAddress getListenAddress() {
    return Objects.isNull(this.host)
        ? new InetSocketAddress(this.port)
        : new InetSocketAddress(this.host, this.port);
  }
}
