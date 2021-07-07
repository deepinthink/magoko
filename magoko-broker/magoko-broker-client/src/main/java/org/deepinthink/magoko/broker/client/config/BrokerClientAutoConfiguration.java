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

import io.rsocket.RSocket;
import io.rsocket.metadata.WellKnownMimeType;
import org.deepinthink.magoko.broker.client.context.BrokerClientBootstrap;
import org.deepinthink.magoko.broker.client.rsocket.BrokerClientRSocketRequesterBuilderCustomizer;
import org.deepinthink.magoko.broker.client.rsocket.BrokerClientRSocketRequesterWrapBuilder;
import org.deepinthink.magoko.broker.client.rsocket.BrokerClientRSocketRequesterWrapBuilderCustomizer;
import org.deepinthink.magoko.broker.client.rsocket.loadbalance.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(BrokerClientMarkerConfiguration.Marker.class)
@ConditionalOnClass({RSocketRequester.class, RSocket.class, RSocketStrategies.class})
@EnableConfigurationProperties(BrokerClientProperties.class)
@AutoConfigureAfter(RSocketRequesterAutoConfiguration.class)
public class BrokerClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientLoadbalanceStrategyProvider
      brokerClientRoundRobinLoadbalanceStrategyProvider() {
    return BrokerClientRoundRobinLoadbalanceStrategy::new;
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketPool brokerClientRSocketPool(
      ObjectProvider<BrokerClientLoadbalanceStrategyProvider> providers) {
    BrokerClientLoadbalanceStrategyProvider
        provider = // using Round-Robin as default loadbalance strategy
        providers.getIfAvailable(this::brokerClientRoundRobinLoadbalanceStrategyProvider);
    BrokerClientLoadbalanceStrategy loadbalanceStrategy = provider.get();
    return BrokerClientRSocketPool.create(loadbalanceStrategy);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientLoadbalancedRSocket brokerClientLoadbalancedRSocket(
      BrokerClientRSocketPool rSocketPool) {
    return BrokerClientLoadbalancedRSocket.create(rSocketPool);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientBootstrap brokerClientBootstrap(
      RSocketRequester.Builder builder, // inject from RSocketRequesterAutoConfiguration
      BrokerClientRSocketPool rSocketPool,
      BrokerClientProperties properties,
      ObjectProvider<BrokerClientRSocketRequesterBuilderCustomizer> customizers) {
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return new BrokerClientBootstrap(builder, rSocketPool, properties.getServerTargets());
  }

  @Bean
  @Scope("prototype")
  @ConditionalOnMissingBean
  public BrokerClientRSocketRequesterWrapBuilder brokerClientRSocketRequesterBuilder(
      BrokerClientLoadbalancedRSocket loadbalancedRSocket,
      RSocketStrategies strategies, //  inject from RSocketStrategiesAutoConfiguration
      ObjectProvider<BrokerClientRSocketRequesterWrapBuilderCustomizer> customizers) {
    BrokerClientRSocketRequesterWrapBuilder builder =
        BrokerClientRSocketRequesterWrapBuilder.newBuilder(loadbalancedRSocket)
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON) // "application/json"
            .metadataMimeType( // "message/x.rsocket.composite-metadata.v0"
                MimeType.valueOf(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString()))
            .rsocketStrategies(strategies);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return builder;
  }
}
