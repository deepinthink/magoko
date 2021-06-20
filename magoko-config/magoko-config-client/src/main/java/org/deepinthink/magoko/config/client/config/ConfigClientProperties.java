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

@Data
public class ConfigClientProperties {
  private String serverHost;
  private int serverPort;

  private final InstanceConfig instance = new InstanceConfig();

  @Data
  public static class InstanceConfig {
    private boolean enable = DEFAULT_CONFIG_CLIENT_INSTANCE_ENABLE;
    private String route = DEFAULT_CONFIG_CLIENT_INSTANCE_RSOCKET_ROUTE;
  }
}
