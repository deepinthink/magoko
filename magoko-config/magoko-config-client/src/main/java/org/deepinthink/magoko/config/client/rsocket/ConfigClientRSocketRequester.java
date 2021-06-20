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
package org.deepinthink.magoko.config.client.rsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.deepinthink.magoko.boot.bootstrap.BootstrapIdentity;
import org.deepinthink.magoko.boot.bootstrap.BootstrapInstance;
import org.deepinthink.magoko.config.client.config.ConfigClientProperties;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Mono;

public class ConfigClientRSocketRequester {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final MediaType[] SUPPORTED_TYPES = {
    MediaType.APPLICATION_JSON, new MediaType("application", "*+json")
  };
  private final RSocketRequester requester;
  private final ConfigClientProperties ccp;
  private final BootstrapIdentity identity;

  public static ConfigClientRSocketRequester getOrElseSupply(
      ConfigurableBootstrapContext bootstrapContext,
      Supplier<ConfigClientRSocketRequester> supplier) {
    return bootstrapContext.getOrElseSupply(ConfigClientRSocketRequester.class, supplier);
  }

  public static ConfigClientRSocketRequester get(
      ConfigurableBootstrapContext bootstrapContext,
      ConfigClientProperties ccp,
      BootstrapInstance instance) {
    return getOrElseSupply(
        bootstrapContext, () -> ConfigClientRSocketRequester.from(ccp, instance));
  }

  public static ConfigClientRSocketRequester from(
      ConfigClientProperties ccp, BootstrapInstance instance) {
    RSocketRequester requester =
        RSocketRequester.builder()
            .rsocketStrategies(
                RSocketStrategies.builder()
                    .decoder(new Jackson2JsonDecoder(objectMapper, SUPPORTED_TYPES))
                    .encoder(new Jackson2JsonEncoder(objectMapper, SUPPORTED_TYPES))
                    .build())
            .tcp(ccp.getServerHost(), ccp.getServerPort());
    return new ConfigClientRSocketRequester(requester, ccp, instance);
  }

  private ConfigClientRSocketRequester(
      RSocketRequester requester, ConfigClientProperties ccp, BootstrapInstance instance) {
    this.requester = Objects.requireNonNull(requester);
    this.ccp = Objects.requireNonNull(ccp);
    this.identity = Objects.requireNonNull(BootstrapIdentity.from(instance));
  }

  public Mono<Map<String, Object>> requestInstanceConfig() {
    return requester
        .route(ccp.getInstance().getRoute())
        .data(identity)
        .retrieveMono(new ParameterizedTypeReference<Map<String, Object>>() {});
  }
}
