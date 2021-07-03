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
package org.deepinthink.magoko.broker.server.connector;

import org.deepinthink.magoko.broker.server.config.BrokerServerProperties;
import org.deepinthink.magoko.broker.server.config.BrokerServerProperties.Connector;
import org.deepinthink.magoko.broker.server.connector.context.BrokerConnectorServerBootstrap;
import org.deepinthink.magoko.broker.server.connector.rsocket.RSocketBrokerConnectorServerFactory;
import org.deepinthink.magoko.broker.server.connector.rsocket.RSocketBrokerConnectorServerFactoryCustomizer;
import org.deepinthink.magoko.broker.server.handler.BrokerMessageHandlerConfiguration;
import org.deepinthink.magoko.broker.server.handler.BrokerMessageHandlerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration(proxyBeanMethods = false)
@AutoConfigureAfter(BrokerMessageHandlerConfiguration.class)
public class BrokerConnectorServerConfiguration {

  private final Connector properties;

  public BrokerConnectorServerConfiguration(BrokerServerProperties properties) {
    this.properties = properties.getConnector();
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerConnectorServerFactory brokerConnectorServerFactory(
      ObjectProvider<RSocketBrokerConnectorServerFactoryCustomizer> customizers) {
    RSocketBrokerConnectorServerFactory serverFactory = new RSocketBrokerConnectorServerFactory();
    PropertyMapper mapper = PropertyMapper.get();
    mapper.from(properties.getHost()).to(serverFactory::setHost);
    mapper.from(properties.getPort()).to(serverFactory::setPort);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(serverFactory));
    return serverFactory;
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerConnectorServer brokerConnectorServer(
      BrokerConnectorServerFactory serverFactory, BrokerMessageHandlerRegistry registry) {
    return serverFactory.createServer(registry);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerConnectorServerBootstrap brokerConnectorServerBootstrap(
      BrokerConnectorServer server) {
    return new BrokerConnectorServerBootstrap(server);
  }
}
