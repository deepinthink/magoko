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
package org.deepinthink.magoko.config.client.env;

import static org.deepinthink.magoko.config.client.ConfigClientConstants.*;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.deepinthink.magoko.boot.bootstrap.BootstrapInstance;
import org.deepinthink.magoko.config.client.ConfigClientRSocketRequester;
import org.deepinthink.magoko.config.client.config.ConfigClientProperties;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class ConfigClientExcelEnvPostProcessor extends AbstractConfigClientEnvPostProcessor {
  public ConfigClientExcelEnvPostProcessor(
      DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
    super(logFactory, bootstrapContext);
  }

  @Override
  public int getOrder() {
    return DEFAULT_CONFIG_CLIENT_EXCEL_ENV_POST_PROCESSOR_ORDER;
  }

  @Override
  protected void doPostProcessEnvironment(
      ConfigurableEnvironment environment,
      SpringApplication application,
      ConfigurableBootstrapContext bootstrapContext,
      ConfigClientProperties ccp,
      BootstrapInstance instance) {
    if (ccp.getExcel().isEnable()) {
      ConfigClientRSocketRequester requester =
          ConfigClientRSocketRequester.get(bootstrapContext, ccp, instance);
      Map<String, Object> map =
          requester
              .requestExcelConfig()
              .doOnError(logger::error)
              .onErrorReturn(Collections.emptyMap())
              .block();
      if (Objects.nonNull(map)) {
        MapPropertySource mps =
            new MapPropertySource(DEFAULT_CONFIG_CLIENT_EXCEL_PROPERTY_NAME, map);
        environment.getPropertySources().addFirst(mps);
        if (ccp.getExcel().isAutoRelease()) {
          application.addListeners(
              (applicationEvent) -> {
                if (applicationEvent instanceof ApplicationReadyEvent) {
                  environment
                      .getPropertySources()
                      .remove(DEFAULT_CONFIG_CLIENT_EXCEL_PROPERTY_NAME);
                }
              });
        }
      }
    }
  }
}
