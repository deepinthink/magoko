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
package org.deepinthink.magoko.boot.msocket;

import org.deepinthink.magoko.boot.msocket.config.MSocketProperties.TransportType;

public final class MSocketConstants {
  public static final String PREFIX = "magoko.boot.msocket";

  public static final String DEFAULT_MSOCKET_SERVER_WS_MAPPING_PATH =
      System.getProperty(PREFIX + ".server.mapping-path", "/ws");

  public static final TransportType DEFAULT_MSOCKET_SERVER_TRANSPORT_TYPE =
      TransportType.valueOf(System.getProperty(PREFIX + ".server.transport-type", "TCP"));

  public static final String DEFAULT_MSOCKET_SERVER_DAEMON_THREAD_NAME =
      System.getProperty(PREFIX + ".server.daemon-await-thread-name", "msocket-server");

  private MSocketConstants() {}
}
