/**
 * Copyright 2016-2019 Cloudera, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.hortonworks.registries.cache;


import com.hortonworks.registries.cache.stats.CacheStats;
import com.hortonworks.registries.cache.view.config.ExpiryPolicy;

import java.util.Collection;
import java.util.Map;


public interface Cache<K, V> {
    V get(K key);

    Map<K, V> getAll(Collection<? extends K> keys);

    void put(K key, V val);

    void putAll(Map<? extends K, ? extends V> entries);

    void remove(K key);

    void removeAll(Collection<? extends K> keys);

    void clear();

    long size();

    CacheStats stats();

    ExpiryPolicy getExpiryPolicy();
}
