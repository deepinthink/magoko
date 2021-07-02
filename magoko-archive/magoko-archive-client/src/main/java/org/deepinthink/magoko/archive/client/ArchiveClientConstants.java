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
package org.deepinthink.magoko.archive.client;

import org.deepinthink.magoko.boot.bootstrap.BootstrapLaunchMode;

public final class ArchiveClientConstants {
  public static final String PREFIX = "magoko.archive.client";

  public static final String ARCHIVE_CLIENT_RSOCKET_REQUESTER_BEAN_NAME =
      "ArchiveClientRSocketRequester";

  public static final BootstrapLaunchMode DEFAULT_ARCHIVE_CLIENT_LAUNCH_MODE =
      BootstrapLaunchMode.valueOf(System.getProperty(PREFIX + ".launch-mode", "STANDALONE"));

  public static final String DEFAULT_ARCHIVE_CLIENT_STANDALONE_SERVER_HOST =
      System.getProperty(PREFIX + ".standalone.server-host", "localhost");

  public static final int DEFAULT_ARCHIVE_CLIENT_STANDALONE_SERVER_PORT =
      Integer.getInteger(PREFIX + ".standalone.server-port", 8003);

  public static final String DEFAULT_ARCHIVE_CLIENT_STANDALONE_SETUP_ROUTE =
      System.getProperty(PREFIX + ".standalone.setup-route", "magoko.archive.standalone.connect");

  private ArchiveClientConstants() {}
}
