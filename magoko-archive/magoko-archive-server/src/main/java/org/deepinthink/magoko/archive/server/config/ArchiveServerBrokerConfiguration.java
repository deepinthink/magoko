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
package org.deepinthink.magoko.archive.server.config;

import java.util.Collections;
import org.deepinthink.magoko.archive.server.condition.ConditionalOnArchiveServerBroker;
import org.deepinthink.magoko.archive.server.controller.ArchiveServerRSocketController;
import org.deepinthink.magoko.broker.client.BrokerClientRSocketHandlersProvider;
import org.deepinthink.magoko.broker.client.context.BrokerClientRSocketRequesterBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass(BrokerClientRSocketRequesterBootstrap.class)
@ConditionalOnArchiveServerBroker
public class ArchiveServerBrokerConfiguration {

  private final ApplicationContext applicationContext;

  @Autowired
  public ArchiveServerBrokerConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketHandlersProvider archiveServerBrokerRSocketHandlerProvider() {
    return () ->
        Collections.singletonList(
            this.applicationContext.getBean(ArchiveServerRSocketController.class));
  }
}
