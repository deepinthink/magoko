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
package org.deepinthink.magoko.pay.client.config;

import org.deepinthink.magoko.broker.client.config.BrokerClientAutoConfiguration;
import org.deepinthink.magoko.broker.client.rsocket.BrokerClientRSocketRequesterBuilder;
import org.deepinthink.magoko.pay.client.rsocket.LoginClientRSocketRequesterBuilderCustomizer;
import org.deepinthink.magoko.pay.client.template.PayClientTemplate;
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
@ConditionalOnClass({BrokerClientRSocketRequesterBuilder.class, RSocketRequester.class})
@ConditionalOnBean(PayClientMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(PayClientProperties.class)
@AutoConfigureAfter(BrokerClientAutoConfiguration.class)
public class PayClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public PayClientTemplate payClientTemplate(
      BrokerClientRSocketRequesterBuilder builder,
      ObjectProvider<LoginClientRSocketRequesterBuilderCustomizer> customizers) {
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return PayClientTemplate.create(builder.build());
  }
}
