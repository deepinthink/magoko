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
package org.deepinthink.magoko.boot.bootstrap.env;

import static org.deepinthink.magoko.boot.bootstrap.BootstrapConstants.*;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class BootstrapEnvPostProcessor implements EnvironmentPostProcessor, Ordered {

  private final Log logger;

  public BootstrapEnvPostProcessor(DeferredLogFactory logFactory) {
    this.logger = logFactory.getLog(getClass());
  }

  @Override
  public int getOrder() {
    return DEFAULT_BOOTSTRAP_ENV_POST_PROCESSOR_ORDER;
  }

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    try {
      InputStream inputStream =
          getClass().getResourceAsStream(DEFAULT_BOOTSTRAP_DEFAULT_CONFIG_FILE);
      if (Objects.nonNull(inputStream)) {
        Properties properties = new Properties();
        properties.load(inputStream);
        if (!properties.isEmpty()) {
          PropertiesPropertySource pps =
              new PropertiesPropertySource(
                  DEFAULT_BOOTSTRAP_DEFAULT_PROPERTY_SOURCE_NAME, properties);
          environment.getPropertySources().addFirst(pps);
        }
      } else {
        logger.warn("CAN NOT Find " + DEFAULT_BOOTSTRAP_DEFAULT_CONFIG_FILE);
      }
    } catch (Exception e) {
      logger.error("Load " + DEFAULT_BOOTSTRAP_DEFAULT_CONFIG_FILE + " failed", e);
    }
  }
}
