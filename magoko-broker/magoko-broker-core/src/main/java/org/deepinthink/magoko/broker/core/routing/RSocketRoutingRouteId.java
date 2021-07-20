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

import java.util.Objects;
import java.util.UUID;

public class RSocketRoutingRouteId {
  private long msb;
  private long lsb;

  public static RSocketRoutingRouteId from(long[] parts) {
    if (Objects.isNull(parts) || parts.length != 2) {
      throw new IllegalArgumentException("parts must have 2 elements");
    }
    return new RSocketRoutingRouteId(parts[0], parts[1]);
  }

  public static RSocketRoutingRouteId from(UUID uuid) {
    Objects.requireNonNull(uuid);
    return new RSocketRoutingRouteId(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
  }

  public static RSocketRoutingRouteId random() {
    return from(UUID.randomUUID());
  }

  public static RSocketRoutingRouteId from(String uuid) {
    return from(UUID.fromString(uuid));
  }

  public RSocketRoutingRouteId(long msb, long lsb) {
    this.msb = msb;
    this.lsb = lsb;
  }

  public long getMsb() {
    return msb;
  }

  public long getLsb() {
    return lsb;
  }

  @Override
  public String toString() {
    return "RSocketRoutingRouteId{" + new UUID(msb, lsb) + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RSocketRoutingRouteId that = (RSocketRoutingRouteId) o;
    return msb == that.msb && lsb == that.lsb;
  }

  @Override
  public int hashCode() {
    return Objects.hash(msb, lsb);
  }
}
