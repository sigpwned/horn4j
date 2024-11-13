/*-
 * =================================LICENSE_START==================================
 * horn4j
 * ====================================SECTION=====================================
 * Copyright (C) 2024 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.horn4j.util;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public final class Maps {
  private Maps() {}

  public static <K, V> Map<K, V> of() {
    return emptyMap();
  }

  public static <K, V> Map<K, V> of(K k1, V v1) {
    return singletonMap(k1, v1);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
    Map<K, V> map = new HashMap<>(2);
    map.put(k1, v1);
    map.put(k2, v2);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    Map<K, V> map = new HashMap<>(3);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    Map<K, V> map = new HashMap<>(4);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    Map<K, V> map = new HashMap<>(5);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    map.put(k5, v5);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
      K k6, V v6) {
    Map<K, V> map = new HashMap<>(6);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    map.put(k5, v5);
    map.put(k6, v6);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
      K k6, V v6, K k7, V v7) {
    Map<K, V> map = new HashMap<>(7);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    map.put(k5, v5);
    map.put(k6, v6);
    map.put(k7, v7);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
      K k6, V v6, K k7, V v7, K k8, V v8) {
    Map<K, V> map = new HashMap<>(8);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    map.put(k5, v5);
    map.put(k6, v6);
    map.put(k7, v7);
    map.put(k8, v8);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
    Map<K, V> map = new HashMap<>(9);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    map.put(k5, v5);
    map.put(k6, v6);
    map.put(k7, v7);
    map.put(k8, v8);
    map.put(k9, v9);
    return unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
      K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
    Map<K, V> map = new HashMap<>(10);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    map.put(k5, v5);
    map.put(k6, v6);
    map.put(k7, v7);
    map.put(k8, v8);
    map.put(k9, v9);
    map.put(k10, v10);
    return unmodifiableMap(map);
  }

  @SafeVarargs
  public static <K, V> Map<K, V> ofEntries(Map.Entry<K, V>... entries) {
    if (entries == null)
      throw new NullPointerException();
    Map<K, V> map = new HashMap<>(entries.length);
    for (Map.Entry<K, V> entry : entries) {
      map.put(entry.getKey(), entry.getValue());
    }
    return unmodifiableMap(map);
  }

  @SuppressWarnings("serial")
  public static <K, V> Map.Entry<K, V> entry(K k, V v) {
    return new AbstractMap.SimpleEntry<K, V>(k, v) {
      @Override
      public V setValue(V value) {
        throw new UnsupportedOperationException();
      }
    };
  }
}
