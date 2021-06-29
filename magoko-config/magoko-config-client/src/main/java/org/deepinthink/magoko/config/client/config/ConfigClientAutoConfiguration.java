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

import static org.deepinthink.magoko.config.client.ConfigClientConstants.DEFAULT_CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME;

import java.util.Objects;
import org.deepinthink.magoko.config.client.rsocket.ConfigClientRSocketRequester;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(ConfigClientRSocketRequester.class)
@AutoConfigureAfter(RSocketStrategiesAutoConfiguration.class)
public class ConfigClientAutoConfiguration {

  @Bean(DEFAULT_CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME)
  @ConditionalOnMissingBean(name = DEFAULT_CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME)
  public RSocketRequester configClientRSocketRequester(
      ConfigClientRSocketRequester configRequester, RSocketStrategies rSocketStrategies) {
    RSocketRequester requester = configRequester.getRequester();
    return RSocketRequester.wrap(
        Objects.requireNonNull(requester.rsocket()),
        requester.dataMimeType(),
        requester.metadataMimeType(),
        rSocketStrategies);
  }
}
