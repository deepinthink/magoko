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
package org.deepinthink.magoko.boot.msocket.config;

import static org.deepinthink.magoko.boot.msocket.MSocketConstants.PREFIX;

import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketConnectionAcceptor;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketServerFactory;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketServerFactoryCustomizer;
import org.deepinthink.magoko.boot.msocket.server.MSocketServerFactory;
import org.deepinthink.magoko.boot.msocket.server.MSocketServerProperties;
import org.deepinthink.magoko.boot.msocket.server.context.MSocketServerBootstrap;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableConfigurationProperties(MSocketProperties.class)
@ConditionalOnProperty(prefix = PREFIX + ".server", name = "port")
public class MSocketServerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public MSocketServerProperties mSocketServerProperties(MSocketProperties properties) {
    return properties.getServer();
  }

  @Bean
  @ConditionalOnMissingBean
  public MSocketServerFactory mSocketServerFactory(
      MSocketServerProperties properties,
      NettyMSocketConnectionAcceptor connectionAcceptor,
      ObjectProvider<NettyMSocketServerFactoryCustomizer> customizers) {
    NettyMSocketServerFactory serverFactory = new NettyMSocketServerFactory(connectionAcceptor);
    PropertyMapper mapper = PropertyMapper.get();
    mapper.from(properties::getHost).to(serverFactory::setHost);
    mapper.from(properties::getPort).to(serverFactory::setPort);
    mapper.from(properties::getTransportType).to(serverFactory::setTransportType);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(serverFactory));
    return serverFactory;
  }

  @Bean
  @ConditionalOnMissingBean
  public MSocketServerBootstrap mSocketServerBootstrap(MSocketServerFactory serverFactory) {
    return new MSocketServerBootstrap(serverFactory);
  }
}
