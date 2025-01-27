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

package com.hortonworks.registries.storage;

import com.hortonworks.registries.cache.Cache;
import com.hortonworks.registries.common.QueryParam;
import com.hortonworks.registries.storage.cache.impl.GuavaCache;
import com.hortonworks.registries.storage.cache.writer.StorageWriter;
import com.hortonworks.registries.storage.exception.StorageException;
import com.hortonworks.registries.storage.search.SearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;


public class CacheBackedStorageManager implements StorageManager {
    private static final Logger LOG = LoggerFactory.getLogger(CacheBackedStorageManager.class);

    private final StorageWriter writer;
    private final Cache<StorableKey, Storable> cache;
    private final StorageManager dao;

    public CacheBackedStorageManager(Cache<StorableKey, Storable> cache, StorageWriter storageWriter) {
        if (cache == null || storageWriter == null) {
            throw new IllegalArgumentException("Cache and storage writer objects must not be null");
        }
        this.cache = cache;
        this.dao = ((GuavaCache) cache).getDao();
        this.writer = storageWriter;
    }

    @Override
    public void init(StorageProviderConfiguration properties) {

    }

    //TODO: Exception handling in add, remove, addOrUpdate, ...
    @Override
    public void add(Storable storable) throws StorageException {
        writer.add(storable);
        if (storable.isCacheable()) {
            cache.put(storable.getStorableKey(), storable);
        }
    }

    @Override
    public <T extends Storable> T remove(StorableKey key) throws StorageException {
        Storable storable = (Storable) writer.remove(key);
        if (storable != null && storable.isCacheable()) {
            Storable cachedStorable = cache.get(key);
            if (!storable.equals(cachedStorable)) {
                LOG.warn("Possible cache inconsistency. Storable from DB '{}', Storable from cache '{}'",
                        storable, cachedStorable);
            }
            cache.remove(key);
        }
        return (T) storable;
    }

    @Override
    public void addOrUpdate(Storable storable) throws StorageException {
        writer.addOrUpdate(storable);
        if (storable.isCacheable()) {
            cache.put(storable.getStorableKey(), storable);
        }
    }

    @Override
    public void update(Storable storable) {
        writer.update(storable);
        if (storable.isCacheable()) {
            cache.put(storable.getStorableKey(), storable);
        }
    }

    @Override
    public <T extends Storable> T get(StorableKey key) throws StorageException {
        Storable storable = cache.get(key);
        // we could load the entries via a LoadingCache so need to explicitly remove it
        if (storable != null && !storable.isCacheable()) {
            cache.remove(key);
        }
        return (T) storable;
    }

    @Override
    public <T extends Storable> Collection<T> find(String namespace, List<QueryParam> queryParams) throws StorageException {
        //Adding workaround methods that calls dao until we figure out what needs to be in caching so the topologies can work.
        return dao.find(namespace, queryParams);

    }

    @Override
    public <T extends Storable> Collection<T> find(String namespace, List<QueryParam> queryParams, List<OrderByField> orderByFields) 
            throws StorageException {
        return ((GuavaCache) cache).getDao().find(namespace, queryParams, orderByFields);
    }

    @Override
    public <T extends Storable> Collection<T> search(SearchQuery searchQuery) {
        return ((GuavaCache) cache).getDao().search(searchQuery);
    }

    @Override
    public <T extends Storable> Collection<T> list(String namespace) throws StorageException {
        return dao.list(namespace);
    }

    @Override
    public void cleanup() throws StorageException {
//        writer.removeAll();       // TODO:
        cache.clear();
    }

    @Override
    public Long nextId(String namespace) throws StorageException {
        return dao.nextId(namespace);
    }

    @Override
    public void registerStorables(Collection<Class<? extends Storable>> classes) throws StorageException {
        dao.registerStorables(classes);
    }

    public StorageManager getStorageManager() {
        return dao;
    }
}
