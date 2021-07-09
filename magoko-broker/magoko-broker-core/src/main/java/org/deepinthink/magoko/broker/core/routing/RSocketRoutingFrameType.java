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

/**
 * https://github.com/rsocket/rsocket/blob/feature/rf/Extensions/Routing-And-Forwarding.md#framing-header-format
 */
public enum RSocketRoutingFrameType {
  RESERVED(0x00), // reserved
  ROUTE_SETUP(0x01); // service setup up metadata frame

  private static RSocketRoutingFrameType[] FRAME_TYPES;

  static {
    FRAME_TYPES = new RSocketRoutingFrameType[values().length];

    for (RSocketRoutingFrameType frameType : values()) {
      FRAME_TYPES[frameType.id] = frameType;
    }
  }

  public static RSocketRoutingFrameType from(int id) {
    return FRAME_TYPES[id];
  }

  private final int id;

  RSocketRoutingFrameType(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
