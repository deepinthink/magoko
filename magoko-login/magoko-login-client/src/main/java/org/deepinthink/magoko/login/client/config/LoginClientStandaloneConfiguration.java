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

import static org.deepinthink.magoko.login.client.LoginClientConstants.LOGIN_CLIENT_RSOCKET_REQUESTER_BEAN_NAME;

import org.deepinthink.magoko.boot.bootstrap.BootstrapIdentity;
import org.deepinthink.magoko.login.client.condition.ConditionalOnLoginClientStandalone;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnLoginClientStandalone
public class LoginClientStandaloneConfiguration {

  @Bean(LOGIN_CLIENT_RSOCKET_REQUESTER_BEAN_NAME)
  @ConditionalOnMissingBean(name = LOGIN_CLIENT_RSOCKET_REQUESTER_BEAN_NAME)
  public RSocketRequester loginClientStandaloneRSocketRequester(
      RSocketRequester.Builder builder,
      LoginClientProperties properties,
      BootstrapIdentity identity) {
    LoginClientProperties.Standalone standalone = properties.getStandalone();
    return builder
        .setupData(identity)
        .setupRoute(standalone.getSetupRoute())
        .tcp(standalone.getServerHost(), standalone.getServerPort());
  }
}
