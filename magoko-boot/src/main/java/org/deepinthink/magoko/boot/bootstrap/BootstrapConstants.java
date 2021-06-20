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
package org.deepinthink.magoko.boot.bootstrap;

import org.springframework.core.Ordered;

public final class BootstrapConstants {
  public static final String PREFIX = "magoko.boot.bootstrap";

  public static final int DEFAULT_BOOTSTRAP_ENV_POST_PROCESSOR_ORDER =
      Integer.getInteger(PREFIX + ".order", Ordered.HIGHEST_PRECEDENCE);
  public static final String DEFAULT_BOOTSTRAP_CONFIG_FILE =
      System.getProperty(PREFIX + ".config-file", "magoko.properties");
  public static final String DEFAULT_BOOTSTRAP_PROPERTY_SOURCE_NAME =
      System.getProperty(PREFIX + "property-source", "magoko.properties");

  public static final BootstrapLaunchMode DEFAULT_BOOTSTRAP_LAUNCH_MODE =
      BootstrapLaunchMode.valueOf(System.getProperty(PREFIX + ".launch-mode", "STANDALONE"));

  private BootstrapConstants() {}
}
