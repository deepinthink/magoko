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
package org.deepinthink.magoko.broker.server.routing;

import java.util.stream.Collectors;
import org.deepinthink.magoko.broker.core.routing.config.BrokerRSocketStrategiesAutoConfiguration;
import org.deepinthink.magoko.broker.server.core.query.BrokerRSocketQuery;
import org.deepinthink.magoko.broker.server.core.rsocket.BrokerCompositeRSocketLocator;
import org.deepinthink.magoko.broker.server.core.rsocket.BrokerMulticastRSocketLocator;
import org.deepinthink.magoko.broker.server.core.rsocket.BrokerRSocketLocator;
import org.deepinthink.magoko.broker.server.core.rsocket.BrokerUnicastRSocketLocator;
import org.deepinthink.magoko.broker.server.routing.rsocket.BrokerRoutingAddressExtractor;
import org.deepinthink.magoko.broker.server.routing.rsocket.BrokerRoutingRSocketFactory;
import org.deepinthink.magoko.broker.server.routing.rsocket.BrokerRoutingRSocketIndex;
import org.deepinthink.magoko.broker.server.routing.rsocket.RSocketBrokerServerRoutingAcceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.rsocket.RSocketStrategies;

@SpringBootConfiguration(proxyBeanMethods = false)
@AutoConfigureAfter(BrokerRSocketStrategiesAutoConfiguration.class)
public class BrokerServerRoutingConfiguration {

  @Bean
  public BrokerRoutingRSocketIndex brokerRSocketRoutingIndex() {
    return new BrokerRoutingRSocketIndex();
  }

  @Bean
  public BrokerMulticastRSocketLocator brokerMulticastRSocketLocator(
      BrokerRSocketQuery rSocketQuery) {
    return new BrokerMulticastRSocketLocator(rSocketQuery);
  }

  @Bean
  public BrokerUnicastRSocketLocator brokerUnicastRSocketLocator(BrokerRSocketQuery rSocketQuery) {
    return new BrokerUnicastRSocketLocator(rSocketQuery);
  }

  @Bean
  @Primary
  public BrokerCompositeRSocketLocator brokerCompositeRSocketLocator(
      ObjectProvider<BrokerRSocketLocator> locators) {
    return new BrokerCompositeRSocketLocator(locators.orderedStream().collect(Collectors.toList()));
  }

  @Bean
  public BrokerRoutingAddressExtractor brokerRoutingAddressExtractor(
      RSocketStrategies rSocketStrategies) {
    return new BrokerRoutingAddressExtractor(rSocketStrategies.metadataExtractor());
  }

  @Bean
  public BrokerRoutingRSocketFactory brokerRoutingRSocketFactory(
      BrokerRSocketLocator rSocketLocator, BrokerRoutingAddressExtractor addressExtractor) {
    return new BrokerRoutingRSocketFactory(rSocketLocator, addressExtractor);
  }

  @Bean
  public BrokerServerRoutingAcceptor brokerServerRoutingAcceptor(
      BrokerRoutingRSocketIndex routingIndex,
      BrokerRoutingRSocketFactory routingRSocketFactory,
      RSocketStrategies rSocketStrategies) {
    return new RSocketBrokerServerRoutingAcceptor(
        routingIndex, routingRSocketFactory, rSocketStrategies.metadataExtractor());
  }
}
