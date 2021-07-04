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
package org.deepinthink.magoko.boot.msocket.server;

import static org.deepinthink.magoko.boot.msocket.MSocketConstants.DEFAULT_SERVER_MAPPING_PATH;
import static org.deepinthink.magoko.boot.msocket.MSocketConstants.DEFAULT_SERVER_TRANSPORT_TYPE;

import java.net.InetAddress;
import org.deepinthink.magoko.boot.msocket.MSocketTransportType;

public class MSocketServerProperties {
  private InetAddress host;
  private int port;
  private MSocketTransportType transportType = DEFAULT_SERVER_TRANSPORT_TYPE;
  private String mappingPath = DEFAULT_SERVER_MAPPING_PATH;

  public InetAddress getHost() {
    return host;
  }

  public void setHost(InetAddress host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public MSocketTransportType getTransportType() {
    return transportType;
  }

  public void setTransportType(MSocketTransportType transportType) {
    this.transportType = transportType;
  }

  public String getMappingPath() {
    return mappingPath;
  }

  public void setMappingPath(String mappingPath) {
    this.mappingPath = mappingPath;
  }
}
