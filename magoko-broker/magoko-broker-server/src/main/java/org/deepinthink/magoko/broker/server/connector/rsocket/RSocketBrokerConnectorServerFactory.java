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
package org.deepinthink.magoko.broker.server.connector.rsocket;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import org.deepinthink.magoko.broker.server.connector.BrokerConnectorServer;
import org.deepinthink.magoko.broker.server.connector.BrokerConnectorServerFactory;
import org.deepinthink.magoko.broker.server.connector.ConfigurableBrokerConnectorServerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpServer;

public class RSocketBrokerConnectorServerFactory
    implements BrokerConnectorServerFactory, ConfigurableBrokerConnectorServerFactory {

  private InetAddress host;
  private int port;
  private Duration lifecycleTimeout;

  @Override
  public BrokerConnectorServer createServer(SocketAcceptor socketAcceptor) {
    TcpServer tcpServer = TcpServer.create().bindAddress(this::getListenAddress);
    TcpServerTransport tcpServerTransport = TcpServerTransport.create(tcpServer);
    Mono<CloseableChannel> starter =
        RSocketServer.create().acceptor(socketAcceptor).bind(tcpServerTransport);
    return new RSocketBrokerConnectorServer(starter, this.lifecycleTimeout);
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
  public void setLifecycleTimeout(Duration lifecycleTimeout) {
    this.lifecycleTimeout = lifecycleTimeout;
  }

  public InetSocketAddress getListenAddress() {
    return Objects.nonNull(this.host)
        ? new InetSocketAddress(this.host, this.port)
        : new InetSocketAddress(this.port);
  }
}
