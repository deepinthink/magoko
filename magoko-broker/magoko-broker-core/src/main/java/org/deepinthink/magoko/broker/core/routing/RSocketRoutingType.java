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

import java.util.HashMap;
import java.util.Map;
import org.deepinthink.magoko.broker.core.routing.codec.RSocketRoutingAddressCodec;

public enum RSocketRoutingType {
  UNICAST(RSocketRoutingAddressCodec.FLAGS_U),
  MULTICAST(RSocketRoutingAddressCodec.FLAGS_M);

  private static final Map<Integer, RSocketRoutingType> ROUTING_TYPES;

  static {
    ROUTING_TYPES = new HashMap<>();

    for (RSocketRoutingType routingType : values()) {
      ROUTING_TYPES.put(routingType.getFlag(), routingType);
    }
  }

  private final int flag;

  RSocketRoutingType(int flag) {
    this.flag = flag;
  }

  public int getFlag() {
    return flag;
  }

  public static RSocketRoutingType from(int flag) {
    return ROUTING_TYPES.get(flag);
  }
}
