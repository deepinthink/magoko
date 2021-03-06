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
package org.deepinthink.magoko.login.client.config;

import org.deepinthink.magoko.broker.client.config.BrokerClientAutoConfiguration;
import org.deepinthink.magoko.broker.client.rsocket.BrokerClientRSocketRequesterWrapBuilder;
import org.deepinthink.magoko.login.client.rsocket.LoginClientRSocketRequesterWrapBuilderCustomizer;
import org.deepinthink.magoko.login.client.template.LoginClientTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass({BrokerClientRSocketRequesterWrapBuilder.class, RSocketRequester.class})
@ConditionalOnBean(LoginClientMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(LoginClientProperties.class)
@AutoConfigureAfter(BrokerClientAutoConfiguration.class)
public class LoginClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public LoginClientTemplate loginClientTemplate(
      BrokerClientRSocketRequesterWrapBuilder builder,
      ObjectProvider<LoginClientRSocketRequesterWrapBuilderCustomizer> customizers) {
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return LoginClientTemplate.create(builder.build());
  }
}
