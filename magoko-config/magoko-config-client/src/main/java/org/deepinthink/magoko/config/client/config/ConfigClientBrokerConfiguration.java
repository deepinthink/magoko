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
package org.deepinthink.magoko.config.client.config;

import static org.deepinthink.magoko.broker.client.BrokerClientConstants.BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME;
import static org.deepinthink.magoko.config.client.ConfigClientConstants.DEFAULT_CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME;

import org.deepinthink.magoko.broker.client.context.BrokerClientRSocketRequesterBootstrap;
import org.deepinthink.magoko.config.client.condition.ConditionalOnConfigClientBroker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(BrokerClientRSocketRequesterBootstrap.class)
@ConditionalOnConfigClientBroker
public class ConfigClientBrokerConfiguration {

  @Bean(DEFAULT_CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME)
  @ConditionalOnMissingBean(name = DEFAULT_CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME)
  public RSocketRequester configClientBrokerRSocketRequester(
      @Qualifier(BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME) RSocketRequester requester) {
    return requester;
  }
}
