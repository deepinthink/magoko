/*
 * Copyright (c) 2022-present DeepInThink. All rights reserved.
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
package org.deepinthink.magoko.boot.autoconfigure;

import com.google.common.util.concurrent.ServiceManager;
import org.deepinthink.magoko.boot.bootstrap.guava.ServiceManagerBootstrap;
import org.deepinthink.magoko.boot.bootstrap.guava.ServiceManagerCustomizer;
import org.deepinthink.magoko.boot.bootstrap.guava.ServiceManagerProperties;
import org.deepinthink.magoko.boot.bootstrap.guava.ServiceRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class BootstrapAutoConfiguration {

  @AutoConfiguration
  @ConditionalOnClass(ServiceManager.class)
  @EnableConfigurationProperties(ServiceManagerProperties.class)
  @ConditionalOnProperty(prefix = ServiceManagerProperties.PREFIX, name = "enabled")
  public static class GuavaServiceConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public static ServiceRegistry serviceRegistry() {
      return new ServiceRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceManager serviceManager(
        ServiceRegistry registry, ObjectProvider<ServiceManagerCustomizer> customizers) {
      ServiceManager serviceManager = new ServiceManager(registry.getBootstraps());
      customizers.orderedStream().forEach(customizer -> customizer.accept(serviceManager));
      return serviceManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceManagerBootstrap serviceManagerBootstrap(
        ServiceManagerProperties properties, ServiceManager serviceManager) {
      return new ServiceManagerBootstrap(
          serviceManager, properties.getStartTimeout(), properties.getStopTimeout());
    }
  }
}
