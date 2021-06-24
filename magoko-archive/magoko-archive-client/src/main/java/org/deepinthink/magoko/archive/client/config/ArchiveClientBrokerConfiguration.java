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
package org.deepinthink.magoko.archive.client.config;

import static org.deepinthink.magoko.archive.client.ArchiveClientConstants.ARCHIVE_CLIENT_RSOCKET_REQUESTER_BEAN_NAME;
import static org.deepinthink.magoko.broker.client.BrokerClientConstants.BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME;

import java.util.Objects;
import org.deepinthink.magoko.archive.client.condition.ConditionalOnArchiveClientBroker;
import org.deepinthink.magoko.broker.client.context.BrokerClientRSocketRequesterBootstrap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass(BrokerClientRSocketRequesterBootstrap.class)
@ConditionalOnArchiveClientBroker
public class ArchiveClientBrokerConfiguration {

  @Bean(ARCHIVE_CLIENT_RSOCKET_REQUESTER_BEAN_NAME)
  @ConditionalOnMissingBean(name = ARCHIVE_CLIENT_RSOCKET_REQUESTER_BEAN_NAME)
  public RSocketRequester archiveClientBrokerRSocketRequester(
      @Qualifier(BROKER_CLIENT_RSOCKET_REQUESTER_BEAN_NAME) RSocketRequester requester,
      RSocketStrategies rSocketStrategies) {
    return RSocketRequester.wrap(
        Objects.requireNonNull(requester.rsocket()),
        requester.dataMimeType(),
        requester.metadataMimeType(),
        rSocketStrategies);
  }
}
