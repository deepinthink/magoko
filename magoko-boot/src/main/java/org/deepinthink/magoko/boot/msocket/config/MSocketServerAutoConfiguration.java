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

import org.deepinthink.magoko.boot.msocket.MSocketServerFactory;
import org.deepinthink.magoko.boot.msocket.condition.ConditionalOnMSocketServer;
import org.deepinthink.magoko.boot.msocket.context.MSocketServerBootstrap;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketServerFactory;
import org.deepinthink.magoko.boot.msocket.netty.NettyMSocketServerFactoryCustomizer;
import org.deepinthink.magoko.boot.msocket.netty.transport.NettyMSocketConnectionAcceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass({TcpServer.class, HttpServer.class})
@AutoConfigureAfter(MSocketMessagingAutoConfiguration.class)
@ConditionalOnMSocketServer
@EnableConfigurationProperties(MSocketProperties.class)
public class MSocketServerAutoConfiguration {

  private final MSocketProperties properties;

  @Autowired
  public MSocketServerAutoConfiguration(MSocketProperties properties) {
    this.properties = properties;
  }

  @Bean
  @ConditionalOnMissingBean
  public NettyMSocketServerFactory nettyMSocketServerFactory(
      NettyMSocketConnectionAcceptor connectionAcceptor,
      ObjectProvider<NettyMSocketServerFactoryCustomizer> customizers) {
    NettyMSocketServerFactory serverFactory = new NettyMSocketServerFactory(connectionAcceptor);
    MSocketProperties.Server server = properties.getServer();
    PropertyMapper mapper = PropertyMapper.get();
    mapper.from(server::getHost).to(serverFactory::setHost);
    mapper.from(server::getPort).to(serverFactory::setPort);
    mapper.from(server::getMappingPath).to(serverFactory::setMappingPath);
    mapper.from(server::getTransportType).to(serverFactory::setTransportType);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(serverFactory));
    return serverFactory;
  }

  @Bean
  @ConditionalOnMissingBean
  public MSocketServerBootstrap mSocketServerBootstrap(MSocketServerFactory serverFactory) {
    return new MSocketServerBootstrap(serverFactory);
  }
}
