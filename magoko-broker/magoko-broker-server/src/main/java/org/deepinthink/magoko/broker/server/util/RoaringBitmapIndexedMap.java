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
package org.deepinthink.magoko.broker.server.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.agrona.collections.Int2ObjectHashMap;
import org.agrona.collections.Object2ObjectHashMap;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingKey;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingRouteId;
import org.deepinthink.magoko.broker.core.routing.RSocketRoutingTags;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;

public class RoaringBitmapIndexedMap<V>
    implements IndexedMap<RSocketRoutingRouteId, V, RSocketRoutingTags> {

  private AtomicInteger internalIndex = new AtomicInteger();

  private final Map<RSocketRoutingRouteId, Integer> keyToIndex; // key
  private final Int2ObjectHashMap<V> indexToValue; // value
  private final Int2ObjectHashMap<RSocketRoutingTags> indexToTags; // tag
  private final Table<RSocketRoutingKey, CharSequence, RoaringBitmap> tagIndexes; // tag indexes

  public RoaringBitmapIndexedMap() {
    this.keyToIndex = Collections.synchronizedMap(new HashMap<>());
    this.indexToValue = new Int2ObjectHashMap<>();
    this.indexToTags = new Int2ObjectHashMap<>();
    this.tagIndexes = new Table<>(new Object2ObjectHashMap<>(), Object2ObjectHashMap::new);
  }

  @Override
  public V get(RSocketRoutingRouteId key) {
    Integer index = this.keyToIndex.get(key);
    return Objects.nonNull(index) ? this.indexToValue.get(index) : null;
  }

  @Override
  public V put(RSocketRoutingRouteId key, V value, RSocketRoutingTags tags) {
    V prevValue;
    int index = this.keyToIndex.computeIfAbsent(key, id -> this.internalIndex.incrementAndGet());
    synchronized (this) {
      prevValue = this.indexToValue.put(index, value);
      this.indexToTags.put(index, tags);
      for (Map.Entry<RSocketRoutingKey, String> tag : tags.entrySet()) {
        RoaringBitmap rbm = this.tagIndexes.get(tag.getKey(), tag.getValue());
        if (Objects.isNull(rbm)) {
          rbm = new RoaringBitmap();
          this.tagIndexes.put(tag.getKey(), tag.getValue(), rbm);
        }
        rbm.add(index);
      }
    }
    return prevValue;
  }

  @Override
  public V remove(RSocketRoutingRouteId key) {
    Integer index = this.keyToIndex.remove(key);
    if (Objects.nonNull(index)) {
      V prevValue = this.indexToValue.remove(index);
      synchronized (this) {
        RSocketRoutingTags tags = this.indexToTags.remove(index);
        if (Objects.nonNull(tags)) {
          remoteTags(index, tags);
        }
      }
      return prevValue;
    }
    return null;
  }

  private void remoteTags(Integer index, RSocketRoutingTags tags) {
    for (Map.Entry<RSocketRoutingKey, String> tag : tags.entrySet()) {
      RoaringBitmap rbm = this.tagIndexes.get(tag.getKey(), tag.getValue());
      if (Objects.nonNull(rbm)) {
        rbm.remove(index);
        if (rbm.isEmpty()) {
          this.tagIndexes.remove(tag.getKey(), tag.getValue());
        }
      }
    }
  }

  @Override
  public int size() {
    return this.indexToValue.size();
  }

  @Override
  public boolean isEmpty() {
    return this.indexToValue.isEmpty();
  }

  @Override
  public void clear() {
    synchronized (this) { // lock self
      this.keyToIndex.clear();
      this.indexToValue.clear();
      this.indexToTags.clear();
    }
  }

  @Override
  public Collection<V> values() {
    return this.indexToValue.values();
  }

  @Override
  public List<V> query(RSocketRoutingTags tags) {
    if (Objects.isNull(tags) || tags.isEmpty()) {
      return new ArrayList<>(this.indexToValue.values()); // return all
    }

    RoaringBitmap result = null;
    for (Map.Entry<RSocketRoutingKey, String> tag : tags.entrySet()) {
      RoaringBitmap rbm = this.tagIndexes.get(tag.getKey(), tag.getValue());
      if (Objects.isNull(rbm)) {
        return Collections.emptyList(); // return empty list
      }

      if (Objects.isNull(result)) {
        result = new RoaringBitmap();
        result.or(rbm); // OR
      } else {
        result.and(rbm); // AND
      }

      if (result.isEmpty()) {
        return Collections.emptyList(); // adding empty is always empty
      }
    }

    return new RoaringBitmapList(result);
  }

  private class RoaringBitmapList extends AbstractList<V> {

    private final RoaringBitmap rbm;

    RoaringBitmapList(RoaringBitmap rbm) {
      this.rbm = Objects.requireNonNull(rbm);
    }

    @Override
    public V get(int index) {
      int key = rbm.select(index);
      return indexToValue.get(key);
    }

    @Override
    public Iterator<V> iterator() {
      return new RoaringBitmapIterator(rbm.getIntIterator());
    }

    @Override
    public int size() {
      return rbm.getCardinality();
    }

    private class RoaringBitmapIterator implements Iterator<V> {

      private final IntIterator intIterator;

      public RoaringBitmapIterator(PeekableIntIterator intIterator) {
        this.intIterator = Objects.requireNonNull(intIterator);
      }

      @Override
      public boolean hasNext() {
        return this.intIterator.hasNext();
      }

      @Override
      public V next() {
        int index = this.intIterator.next();
        return indexToValue.get(index);
      }
    }
  }
}
