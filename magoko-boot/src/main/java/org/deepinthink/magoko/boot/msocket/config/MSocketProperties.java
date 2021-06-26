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
package org.deepinthink.magoko.boot.msocket.config;

import static org.deepinthink.magoko.boot.msocket.MSocketConstants.DEFAULT_MSOCKET_SERVER_TRANSPORT_TYPE;
import static org.deepinthink.magoko.boot.msocket.MSocketConstants.DEFAULT_MSOCKET_SERVER_WS_MAPPING_PATH;

import java.net.InetAddress;
import lombok.Data;
import org.deepinthink.magoko.boot.msocket.MSocketConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = MSocketConstants.PREFIX)
public class MSocketProperties {

  private final Server server = new Server();

  @Data
  public static class Server {
    private InetAddress host;
    private int port;
    private String mappingPath = DEFAULT_MSOCKET_SERVER_WS_MAPPING_PATH;
    private TransportType transportType = DEFAULT_MSOCKET_SERVER_TRANSPORT_TYPE;
  }

  public enum TransportType {
    TCP,
    WEBSOCKET;
  }
}
