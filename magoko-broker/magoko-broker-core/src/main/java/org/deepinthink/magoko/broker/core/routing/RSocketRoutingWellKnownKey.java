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
package org.deepinthink.magoko.broker.core.routing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;

public enum RSocketRoutingWellKnownKey {
  INVALID_KEY("INVALID_KEY_DO_NOT_USE", (byte) -2), // invalid key
  REVERSED_KEY("REVERSED_KEY_DO_NOT_USE", (byte) -1), // reversed key
  NO_TAG("NO_TAG_DO_NOT_USE", (byte) 0x00), // used to indicate there is no tag
  SERVICE_NAME("io.rsocket.routing.ServiceName", (byte) 0x01), // tag for service name
  ROUTE_ID("io.rsocket.routing.RouteId", (byte) 0x02), // Tag for route id
  INSTANCE_NAME("io.rsocket.routing.InstanceName", (byte) 0x03), // Tag for instance name
  LB_METHOD("io.rsocket.routing.LBMethod", (byte) 0x04), // load balance method, eg Round-robin
  TEST_CELL("io.rsocket.routing.TestCell", (byte) 0x05); // A/B testing, eg test, staging, prod

  public static RSocketRoutingWellKnownKey fromIdentifier(int id) {
    return (id < 0x00 || id > 0x7F) ? INVALID_KEY : ID_KEYS[id];
  }

  public static RSocketRoutingWellKnownKey fromMimeType(String mimeType) {
    if (!StringUtils.hasLength(mimeType)) {
      throw new IllegalArgumentException("mimeType must be no-null");
    }
    if (mimeType.equals(REVERSED_KEY.str)) {
      return INVALID_KEY;
    }
    return TYPE_KEYS.getOrDefault(mimeType, INVALID_KEY);
  }

  private static final RSocketRoutingWellKnownKey[] ID_KEYS;
  private static final Map<String, RSocketRoutingWellKnownKey> TYPE_KEYS;

  static {
    ID_KEYS = new RSocketRoutingWellKnownKey[128];
    Arrays.fill(ID_KEYS, REVERSED_KEY);
    TYPE_KEYS = new HashMap<>(128);
    for (RSocketRoutingWellKnownKey wellKnownKey : values()) {
      if (wellKnownKey.getIdentifier() >= 0) {
        ID_KEYS[wellKnownKey.getIdentifier()] = wellKnownKey;
        TYPE_KEYS.put(wellKnownKey.getString(), wellKnownKey);
      }
    }
  }

  private final byte identifier;
  private final String str;
  private final RSocketRoutingKey key;

  RSocketRoutingWellKnownKey(String str, byte identifier) {
    this.identifier = identifier;
    this.str = str;
    this.key = new RSocketRoutingImmutableKey(this);
  }

  public byte getIdentifier() {
    return identifier;
  }

  public String getString() {
    return str;
  }

  public RSocketRoutingKey getKey() {
    return key;
  }

  @Override
  public String toString() {
    return "RSocketRoutingWellKnownKey{"
        + "identifier="
        + identifier
        + ", str='"
        + str
        + '\''
        + ", key="
        + key
        + '}';
  }
}
