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

import static org.deepinthink.magoko.config.client.ConfigClientConstants.CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME;

import java.util.Collection;
import java.util.Collections;
import org.deepinthink.magoko.config.client.ConfigClientRSocketHandlersProvider;
import org.deepinthink.magoko.config.client.ConfigClientRSocketRequester;
import org.deepinthink.magoko.config.client.condition.ConditionalOnConfigClientStandalone;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass({RSocketMessageHandler.class, RSocketStrategies.class})
@ConditionalOnBean(ConfigClientRSocketRequester.class)
@ConditionalOnConfigClientStandalone
public class ConfigClientStandaloneConfiguration {

  @Bean(CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME)
  @ConditionalOnMissingBean(name = CONFIG_CLIENT_RSOCKET_REQUEST_BEAN_NAME)
  public RSocketRequester configClientRSocketRequester(
      ConfigClientRSocketRequester configRequester,
      RSocketStrategies rSocketStrategies,
      ObjectProvider<ConfigClientRSocketHandlersProvider> providers) {
    ConfigClientRSocketHandlersProvider provider =
        providers.getIfAvailable(() -> this::configClientRSocketHandlersProvider);
    Collection<Object> handlers = provider.get();
    if (!handlers.isEmpty()) {
      configRequester.setSocketAcceptor(
          RSocketMessageHandler.responder(rSocketStrategies, provider.get().toArray()));
    }
    return configRequester.getRequester();
  }

  private Collection<Object> configClientRSocketHandlersProvider() {
    return Collections.emptyList();
  }
}
