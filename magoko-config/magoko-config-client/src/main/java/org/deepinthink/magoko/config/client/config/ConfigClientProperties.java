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
package org.deepinthink.magoko.config.client.config;

import static org.deepinthink.magoko.config.client.ConfigClientConstants.*;

import lombok.Data;
import org.deepinthink.magoko.boot.bootstrap.BootstrapLaunchMode;
import org.deepinthink.magoko.config.client.ConfigClientConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = ConfigClientConstants.PREFIX)
public class ConfigClientProperties {
  private BootstrapLaunchMode launchMode = DEFAULT_CONFIG_CLIENT_LAUNCH_MODE;
  private String serverHost;
  private int serverPort;

  private final InstanceConfig instance = new InstanceConfig();
  private final ExcelConfig excel = new ExcelConfig();

  @Data
  public static class InstanceConfig {
    private boolean enable = DEFAULT_CONFIG_CLIENT_INSTANCE_ENABLE;
    private String route = DEFAULT_CONFIG_CLIENT_INSTANCE_RSOCKET_ROUTE;
  }

  @Data
  public static class ExcelConfig {
    private boolean enable = DEFAULT_CONFIG_CLIENT_EXCEL_ENABLE;
    private boolean autoRelease = DEFAULT_CONFIG_CLIENT_EXCEL_AUTO_RELEASE;
    private String route = DEFAULT_CONFIG_CLIENT_EXCEL_RSOCKET_ROUTE;
  }
}
