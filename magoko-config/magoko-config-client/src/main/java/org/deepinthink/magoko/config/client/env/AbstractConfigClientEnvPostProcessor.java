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

import org.apache.commons.logging.Log;
import org.deepinthink.magoko.boot.bootstrap.BootstrapConstants;
import org.deepinthink.magoko.boot.bootstrap.BootstrapInstance;
import org.deepinthink.magoko.config.client.config.ConfigClientProperties;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

public abstract class AbstractConfigClientEnvPostProcessor
    implements EnvironmentPostProcessor, Ordered {
  protected final Log logger;
  private final ConfigurableBootstrapContext bootstrapContext;

  public AbstractConfigClientEnvPostProcessor(
      DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
    this.logger = logFactory.getLog(getClass());
    this.bootstrapContext = bootstrapContext;
  }

  protected abstract void doPostProcessEnvironment(
      ConfigurableEnvironment environment,
      SpringApplication application,
      ConfigurableBootstrapContext bootstrapContext,
      ConfigClientProperties ccp,
      BootstrapInstance instance);

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    if (ClassUtils.isPresent(
        DEFAULT_CONFIG_CLIENT_ENABLE_MARKER_CLASSNAME, getClass().getClassLoader())) {
      try {
        BindResult<BootstrapInstance> instanceRequired =
            Binder.get(environment)
                .bind(BootstrapConstants.PREFIX + ".instance", BootstrapInstance.class);
        if (!instanceRequired.isBound()) {
          logger.error("CAN NOT FOUND BootstrapInstance config properties");
          return;
        }
        BootstrapInstance instance = instanceRequired.get();
        BindResult<ConfigClientProperties> ccpRequired =
            Binder.get(environment).bind(PREFIX, ConfigClientProperties.class);
        if (!ccpRequired.isBound()) {
          logger.error("CAN NOT FOUND ConfigClientProperties config properties");
          return;
        }
        ConfigClientProperties ccp = ccpRequired.get();
        doPostProcessEnvironment(environment, application, this.bootstrapContext, ccp, instance);
      } catch (BindException e) {
        logger.error(e);
      }
    } else {
      logger.error("CAN NOT FOUND magoko-starter-config-client dependency");
    }
  }
}
