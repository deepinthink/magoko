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

public final class MSocketConstants {
  public static final String PREFIX = "magoko.boot.msocket";

  public static final String DEFAULT_SERVER_DAEMON_AWAIT_THREAD_NAME = "MSocketServer";

  public static final MSocketTransportType DEFAULT_SERVER_TRANSPORT_TYPE =
      MSocketTransportType.valueOf(System.getProperty(PREFIX + ".server.transport-type", "TCP"));

  private MSocketConstants() {}
}
