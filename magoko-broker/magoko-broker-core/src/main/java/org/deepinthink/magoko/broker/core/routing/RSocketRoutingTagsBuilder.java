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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RSocketRoutingTagsBuilder<SELF extends RSocketRoutingTagsBuilder<SELF>> {

  private final Map<RSocketRoutingKey, String> tags = new LinkedHashMap<>();

  protected RSocketRoutingTagsBuilder() {}

  public SELF with(String key, String value) {
    Objects.requireNonNull(key);
    if (key.length() > 0x7F) {
      throw new IllegalArgumentException("Key length CAN NOT be greater than 128");
    }
    return with(RSocketRoutingKey.of(key), value);
  }

  public SELF with(RSocketRoutingWellKnownKey wellKnownKey, String value) {
    Objects.requireNonNull(wellKnownKey);
    return with(RSocketRoutingKey.of(wellKnownKey), value);
  }

  @SuppressWarnings("unchecked")
  public SELF with(RSocketRoutingKey key, String value) {
    Objects.requireNonNull(key);
    if (Objects.nonNull(value) && value.length() > 0x7F) {
      throw new IllegalArgumentException("Value length CAN NOT be greater than 128");
    }
    this.tags.put(key, value);
    return (SELF) this;
  }

  @SuppressWarnings("unchecked")
  public SELF with(RSocketRoutingTags tag) {
    this.tags.putAll(tag.asMap());
    return (SELF) this;
  }

  public Map<RSocketRoutingKey, String> getTags() {
    return tags;
  }

  public RSocketRoutingTags buildTags() {
    return new RSocketRoutingTags(Collections.unmodifiableMap(this.tags));
  }
}
