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
package org.deepinthink.magoko.broker.server.proxy;

import static org.deepinthink.magoko.broker.server.BrokerServerConstants.PREFIX;

import io.rsocket.transport.netty.server.TcpServerTransport;
import org.deepinthink.magoko.broker.server.config.BrokerServerProperties;
import org.deepinthink.magoko.broker.server.messaging.BrokerMessagingConfiguration;
import org.deepinthink.magoko.broker.server.messaging.BrokerMessagingHandlerRegistry;
import org.deepinthink.magoko.broker.server.proxy.context.BrokerProxyServerBootstrap;
import org.deepinthink.magoko.broker.server.proxy.rsocket.RSocketBrokerProxyServerFactory;
import org.deepinthink.magoko.broker.server.proxy.rsocket.RSocketBrokerProxyServerFactoryCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import reactor.netty.tcp.TcpServer;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass({TcpServerTransport.class, TcpServer.class})
@AutoConfigureAfter(BrokerMessagingConfiguration.class)
@ConditionalOnProperty(prefix = PREFIX + ".proxy", name = "port")
public class BrokerProxyServerConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public BrokerProxyServerProperties brokerProxyServerProperties(
      BrokerServerProperties properties) {
    return properties.getProxy();
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerProxyServerFactory brokerProxyServerFactory(
      BrokerProxyServerProperties properties,
      ObjectProvider<RSocketBrokerProxyServerFactoryCustomizer> customizers) {
    RSocketBrokerProxyServerFactory serverFactory = new RSocketBrokerProxyServerFactory();
    PropertyMapper mapper = PropertyMapper.get();
    mapper.from(properties::getHost).to(serverFactory::setHost);
    mapper.from(properties::getPort).to(serverFactory::setPort);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(serverFactory));
    return serverFactory;
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerProxyServerBootstrap brokerProxyServerBootstrap(
      BrokerProxyServerFactory serverFactory, BrokerMessagingHandlerRegistry registry) {
    return new BrokerProxyServerBootstrap(serverFactory, registry);
  }
}
