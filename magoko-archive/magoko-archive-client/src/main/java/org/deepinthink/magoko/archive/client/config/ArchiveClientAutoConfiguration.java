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

import static org.deepinthink.magoko.archive.client.ArchiveClientConstants.DEFAULT_RSOCKET_REQUESTER_BEAN_NAME;

import java.util.Objects;
import org.deepinthink.magoko.archive.client.broker.ArchiveClientBrokerConfiguration;
import org.deepinthink.magoko.archive.client.direct.ArchiveClientDirectConfiguration;
import org.deepinthink.magoko.archive.client.template.ArchiveClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(ArchiveClientMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(ArchiveClientProperties.class)
@Import({ArchiveClientBrokerConfiguration.class, ArchiveClientDirectConfiguration.class})
public class ArchiveClientAutoConfiguration {

  private final RSocketRequester rSocketRequester;

  @Autowired
  public ArchiveClientAutoConfiguration(
      @Qualifier(DEFAULT_RSOCKET_REQUESTER_BEAN_NAME) RSocketRequester rSocketRequester) {
    this.rSocketRequester = Objects.requireNonNull(rSocketRequester);
  }

  @Bean
  @ConditionalOnMissingBean
  public ArchiveClientTemplate archiveClientTemplate() {
    return ArchiveClientTemplate.create(rSocketRequester);
  }
}
