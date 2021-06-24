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
package org.deepinthink.magoko.broker.client.config;

import static org.deepinthink.magoko.broker.client.BrokerClientConstants.BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME;

import java.util.Collection;
import java.util.Collections;
import org.deepinthink.magoko.boot.bootstrap.BootstrapIdentity;
import org.deepinthink.magoko.broker.client.BrokerClientRSocketHandlersProvider;
import org.deepinthink.magoko.broker.client.condition.ConditionalOnBrokerClient;
import org.deepinthink.magoko.broker.client.context.BrokerClientRSocketRequesterBootstrap;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Controller;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(BrokerClientMarkerConfiguration.Marker.class)
@ConditionalOnClass({RSocketRequester.class, RSocketStrategies.class})
@AutoConfigureAfter(RSocketStrategiesAutoConfiguration.class)
@ConditionalOnBrokerClient
@EnableConfigurationProperties(BrokerClientProperties.class)
public class BrokerClientAutoConfiguration {

  private final ApplicationContext applicationContext;

  @Autowired
  public BrokerClientAutoConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  private Collection<Object> brokerClientRSocketHandlersProvider() {
    return Collections.unmodifiableCollection(
        this.applicationContext.getBeansWithAnnotation(Controller.class).values());
  }

  @Bean(BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME)
  @ConditionalOnMissingBean(name = BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME)
  public RSocketRequester brokerClientRSocketRequester(
      RSocketRequester.Builder builder,
      BrokerClientProperties properties,
      BootstrapIdentity identity,
      RSocketStrategies rSocketStrategies,
      ObjectProvider<BrokerClientRSocketHandlersProvider> providers) {
    BrokerClientRSocketHandlersProvider provider =
        providers.getIfAvailable(() -> this::brokerClientRSocketHandlersProvider);
    return builder
        .setupData(identity)
        .rsocketStrategies(rSocketStrategies)
        .rsocketConnector(
            connector ->
                connector.acceptor(
                    RSocketMessageHandler.responder(rSocketStrategies, provider.get().toArray())))
        .tcp(properties.getServerHost(), properties.getServerPort());
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketRequesterBootstrap brokerClientRSocketRequesterBootstrap(
      @Qualifier(BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME) RSocketRequester requester) {
    return new BrokerClientRSocketRequesterBootstrap(requester);
  }
}
