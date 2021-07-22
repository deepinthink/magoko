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

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Table<R, C, V> {

  final Map<R, Map<C, V>> backingMap;
  final Supplier<? extends Map<C, V>> factory;

  Table(Map<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory) {
    this.backingMap = Objects.requireNonNull(backingMap);
    this.factory = Objects.requireNonNull(factory);
  }

  public Map<R, Map<C, V>> rowMap() {
    return this.backingMap;
  }

  public boolean contains(Object rowKey, Object columnKey) {
    if (Objects.isNull(rowKey) || Objects.isNull(columnKey)) {
      return false;
    }
    Map<C, V> rowMap = safeGet(rowMap(), rowKey);
    return Objects.nonNull(rowMap) && safeContainsKey(rowMap, columnKey);
  }

  public V get(Object rowKey, Object columnKey) {
    if (Objects.isNull(rowKey) || Objects.isNull(columnKey)) {
      return null;
    }
    Map<C, V> rowMap = safeGet(rowMap(), rowKey);
    return Objects.nonNull(rowMap) ? safeGet(rowMap, columnKey) : null;
  }

  public V put(R rowKey, C columnKey, V value) {
    Objects.requireNonNull(rowKey);
    Objects.requireNonNull(columnKey);
    Objects.requireNonNull(value);
    Map<C, V> rowMap = this.backingMap.get(rowKey);
    if (Objects.isNull(rowMap)) {
      rowMap = factory.get();
      this.backingMap.put(rowKey, rowMap);
    }
    return rowMap.put(columnKey, value);
  }

  public V remove(Object rowKey, Object columnKey) {
    if (Objects.isNull(rowKey) || Objects.isNull(columnKey)) {
      return null;
    }
    Map<C, V> rowMap = safeGet(this.backingMap, rowKey);
    if (Objects.isNull(rowMap)) {
      return null;
    }
    V value = rowMap.get(columnKey);
    if (rowMap.isEmpty()) {
      this.backingMap.remove(rowKey);
    }
    return value;
  }

  public Map<C, V> row(R rowKey) {
    return this.backingMap.get(rowKey);
  }

  public boolean isEmpty() {
    return this.backingMap.isEmpty();
  }

  public int size() {
    return this.backingMap.values().stream().mapToInt(Map::size).sum();
  }

  public void clean() {
    this.backingMap.clear();
  }

  @Override
  public String toString() {
    return this.backingMap.toString();
  }

  static boolean safeContainsKey(Map<?, ?> map, Object key) {
    Objects.requireNonNull(map);
    try {
      return map.containsKey(key);
    } catch (Throwable ignored) {
      return false;
    }
  }

  static <V> V safeGet(Map<?, V> map, Object key) {
    Objects.requireNonNull(map);
    try {
      return map.get(key);
    } catch (Throwable ignored) {
      return null;
    }
  }
}
