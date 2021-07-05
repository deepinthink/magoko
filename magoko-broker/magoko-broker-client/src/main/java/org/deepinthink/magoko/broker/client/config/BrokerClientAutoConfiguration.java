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

import org.deepinthink.magoko.broker.client.condition.ConditionalOnBrokerClient;
import org.deepinthink.magoko.broker.client.context.BrokerClientBootstrap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;

import static org.deepinthink.magoko.broker.client.BrokerClientConstants.DEFAULT_RSOCKET_REQUESTER_BEAN_NAME;
import static org.deepinthink.magoko.broker.client.BrokerClientConstants.PREFIX;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(BrokerClientMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BrokerClientProperties.class)
@ConditionalOnBrokerClient
public class BrokerClientAutoConfiguration {

  @Bean(DEFAULT_RSOCKET_REQUESTER_BEAN_NAME)
  @ConditionalOnMissingBean(name = DEFAULT_RSOCKET_REQUESTER_BEAN_NAME)
  public RSocketRequester brokerClientRSocketRequester(
      BrokerClientProperties properties, RSocketRequester.Builder builder) {
    RSocketRequester rSocketRequester =
        builder.tcp(properties.getServerHost(), properties.getServerPort());
    return rSocketRequester;
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = PREFIX, name = "auto-reconnected", value = "true")
  public BrokerClientBootstrap brokerClientBootstrap(
      @Qualifier(DEFAULT_RSOCKET_REQUESTER_BEAN_NAME) RSocketRequester rSocketRequester) {
    return new BrokerClientBootstrap(rSocketRequester);
  }
}
