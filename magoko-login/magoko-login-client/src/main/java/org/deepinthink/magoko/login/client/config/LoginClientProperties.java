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
package org.deepinthink.magoko.login.client.config;

import static org.deepinthink.magoko.login.client.LoginClientConstants.*;

import lombok.Data;
import org.deepinthink.magoko.boot.bootstrap.BootstrapLaunchMode;
import org.deepinthink.magoko.login.client.LoginClientConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = LoginClientConstants.PREFIX)
public class LoginClientProperties {
  private BootstrapLaunchMode launchMode = DEFAULT_LOGIN_CLIENT_LAUNCH_MODE;
  private final Standalone standalone = new Standalone();

  @Data
  public static class Standalone {
    private String serverHost = DEFAULT_LOGIN_CLIENT_STANDALONE_SERVER_HOST;
    private int serverPort = DEFAULT_LOGIN_CLIENT_STANDALONE_SERVER_PORT;
    private String setupRoute = DEFAULT_LOGIN_CLIENT_STANDALONE_SETUP_ROUTE;
  }
}
