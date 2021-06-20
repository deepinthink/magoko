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
package org.deepinthink.magoko.config.client;

import static org.deepinthink.magoko.boot.bootstrap.BootstrapConstants.DEFAULT_BOOTSTRAP_ENV_POST_PROCESSOR_ORDER;

public final class ConfigClientConstants {
  public static final String PREFIX = "magoko.config.client";

  public static final int DEFAULT_CONFIG_CLIENT_INSTANCE_ENV_POST_PROCESSOR_ORDER =
      Integer.getInteger(PREFIX + ".order", DEFAULT_BOOTSTRAP_ENV_POST_PROCESSOR_ORDER + 1);
  public static final String DEFAULT_CONFIG_CLIENT_ENABLE_MARKER_CLASSNAME =
      "org.deepinthink.magoko.config.client.EnableMarker";
  public static final String DEFAULT_CONFIG_CLIENT_INSTANCE_RSOCKET_ROUTE =
      System.getProperty(PREFIX + ".config-instance-route", "config.instance");
  public static final boolean DEFAULT_CONFIG_CLIENT_INSTANCE_ENABLE =
      Boolean.getBoolean(PREFIX + ".instance.enable");
  public static final String DEFAULT_CONFIG_CLIENT_INSTANCE_PROPERTY_NAME =
      System.getProperty(PREFIX + ".instance.property-source", "config.instance.properties");

  private ConfigClientConstants() {}
}
