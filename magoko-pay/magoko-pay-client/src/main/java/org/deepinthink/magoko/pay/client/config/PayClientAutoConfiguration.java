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

import static org.deepinthink.magoko.pay.client.PayClientConstants.PAY_CLIENT_RSOCKET_REQUESTER_BEAN_NAME;

import org.deepinthink.magoko.pay.client.PayClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(PayClientMarkerConfiguration.Marker.class)
@Import({PayClientBrokerConfiguration.class, PayClientStandaloneConfiguration.class})
@EnableConfigurationProperties(PayClientProperties.class)
public class PayClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public PayClient payClient(
      @Qualifier(PAY_CLIENT_RSOCKET_REQUESTER_BEAN_NAME) RSocketRequester requester) {
    return PayClient.fromRSocketRequester(requester);
  }
}
