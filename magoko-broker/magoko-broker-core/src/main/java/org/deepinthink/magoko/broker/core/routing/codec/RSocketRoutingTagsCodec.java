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
package org.deepinthink.magoko.broker.core.routing.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingKey;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingTags;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingTagsBuilder;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingWellKnownKey;

public final class RSocketRoutingTagsCodec {

  private static final int WELL_KNOWN_TAG = 0x80; // indicate is well known tag
  private static final int HAS_MORE_TAGS = 0x80; // indicate there is remaining data
  private static final int MAX_TAG_LENGTH = 0x7F; // max length

  public static ByteBuf encodeTag(ByteBuf byteBuf, RSocketRoutingTags tag) {
    Objects.requireNonNull(byteBuf);

    Iterator<Map.Entry<RSocketRoutingKey, String>> iterator = tag.asMap().entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<RSocketRoutingKey, String> entry = iterator.next();
      RSocketRoutingKey key = entry.getKey();
      if (Objects.nonNull(key.getWellKnownKey())) {
        byte identifier = key.getWellKnownKey().getIdentifier();
        int keyLength = WELL_KNOWN_TAG | identifier;
        byteBuf.writeByte(keyLength);
      } else {
        String keyString = key.getKey();
        if (Objects.isNull(keyString)) {
          continue;
        }
        int keyLength = ByteBufUtil.utf8Bytes(keyString);
        if (keyLength == 0 || keyLength > MAX_TAG_LENGTH) {
          continue;
        }
        byteBuf.writeByte(keyLength);
        ByteBufUtil.reserveAndWriteUtf8(byteBuf, keyString, keyLength);
      }

      boolean hasMoreTags = iterator.hasNext();
      String value = entry.getValue();
      int valueLength = ByteBufUtil.utf8Bytes(value);
      if (valueLength == 0 || valueLength > MAX_TAG_LENGTH) {
        continue;
      }
      int valueByte;
      if (hasMoreTags) {
        valueByte = HAS_MORE_TAGS | valueLength;
      } else {
        valueByte = valueLength;
      }
      byteBuf.writeByte(valueByte);
      ByteBufUtil.reserveAndWriteUtf8(byteBuf, value, valueLength);
    }
    return byteBuf;
  }

  public static RSocketRoutingTags decodeTag(int offset, ByteBuf byteBuf) {

    if (offset >= byteBuf.writerIndex()) {
      return RSocketRoutingTags.empty();
    }

    RSocketRoutingTagsBuilder<?> tagsBuilder = RSocketRoutingTags.builder();

    boolean hasMoreTags = true;

    while (hasMoreTags) {
      int keyByte = byteBuf.getByte(offset);
      offset += Byte.BYTES;

      boolean isWellKnownTag = (keyByte & WELL_KNOWN_TAG) == WELL_KNOWN_TAG;
      int keyLengthOrId = keyByte & MAX_TAG_LENGTH;

      RSocketRoutingKey key;
      if (isWellKnownTag) {
        RSocketRoutingWellKnownKey wellKnownKey =
            RSocketRoutingWellKnownKey.fromIdentifier(keyLengthOrId);
        key = RSocketRoutingKey.of(wellKnownKey);
      } else {
        String keyString = byteBuf.toString(offset, keyLengthOrId, StandardCharsets.UTF_8);
        offset += keyLengthOrId;
        key = RSocketRoutingKey.of(keyString);
      }

      int valueByte = byteBuf.getByte(offset);
      offset += Byte.BYTES;

      hasMoreTags = (valueByte & HAS_MORE_TAGS) == HAS_MORE_TAGS;
      int valueLength = valueByte & MAX_TAG_LENGTH;
      String value = byteBuf.toString(offset, valueLength, StandardCharsets.UTF_8);
      offset += valueLength;

      tagsBuilder.with(key, value);
    }

    return tagsBuilder.buildTags();
  }

  public static int length(int offset, ByteBuf byteBuf) {

    int originOffset = offset;

    if (offset >= byteBuf.writerIndex()) {
      return 0;
    }

    boolean hasMoreTags = true;

    while (hasMoreTags) {
      int keyByte = byteBuf.getByte(offset);
      offset += Byte.BYTES;

      boolean isWellKnownTag = (keyByte & WELL_KNOWN_TAG) == WELL_KNOWN_TAG;

      if (!isWellKnownTag) {
        int keyLength = keyByte & MAX_TAG_LENGTH;
        offset += keyLength;
      }

      int valueByte = byteBuf.getByte(offset);
      offset += Byte.BYTES;

      hasMoreTags = (valueByte & HAS_MORE_TAGS) == HAS_MORE_TAGS;
      int valueLength = valueByte & MAX_TAG_LENGTH;
      offset += valueLength;
    }

    return offset - originOffset;
  }

  private RSocketRoutingTagsCodec() {}
}
