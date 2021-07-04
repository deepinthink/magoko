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
package org.deepinthink.magoko.broker.server.proxy.context;

import io.rsocket.SocketAcceptor;
import java.util.Objects;
import org.deepinthink.magoko.broker.server.proxy.BrokerProxyServer;
import org.deepinthink.magoko.broker.server.proxy.BrokerProxyServerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

public class BrokerProxyServerBootstrap implements SmartLifecycle, ApplicationContextAware {

  private final BrokerProxyServer proxyServer;
  private ApplicationContext applicationContext;

  public BrokerProxyServerBootstrap(
      BrokerProxyServerFactory serverFactory, SocketAcceptor socketAcceptor) {
    this.proxyServer = Objects.requireNonNull(serverFactory.createServer(socketAcceptor));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void start() {
    this.proxyServer.start();
    this.applicationContext.publishEvent(new BrokerProxyServerInitializedEvent(this.proxyServer));
  }

  @Override
  public void stop() {
    this.proxyServer.stop();
  }

  @Override
  public boolean isRunning() {
    return this.proxyServer.isRunning();
  }
}
