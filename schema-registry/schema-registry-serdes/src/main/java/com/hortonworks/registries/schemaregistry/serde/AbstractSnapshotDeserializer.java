/*
 * Copyright 2016-2019 Cloudera, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hortonworks.registries.schemaregistry.serde;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hortonworks.registries.schemaregistry.SchemaIdVersion;
import com.hortonworks.registries.schemaregistry.SchemaMetadata;
import com.hortonworks.registries.schemaregistry.SchemaVersionInfo;
import com.hortonworks.registries.schemaregistry.SchemaVersionKey;
import com.hortonworks.registries.schemaregistry.client.ISchemaRegistryClient;
import com.hortonworks.registries.schemaregistry.errors.InvalidSchemaException;
import com.hortonworks.registries.schemaregistry.errors.SchemaNotFoundException;
import com.hortonworks.registries.schemaregistry.exceptions.RegistryException;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class implements {@link SnapshotDeserializer} and provides common functionality like
 * <ul>
 *   <li>connecting to given schema registry with schema registry client(internally provides caching for multiple types)</li>
 *   <li>caching of parsed schemas</li>
 *   <li>providing life cycle to deserialization as mentioned below.</li>
 * </ul>
 *
 * Deserialization involves
 * <ol>
 *  <li>getting ser/des protocol id by implementing {@link #retrieveProtocolId(Object)} </li>
 *  <li>getting schema version id by implementing {@link #retrieveSchemaIdVersion(byte, Object)} </li>
 *  <li>implement the actual deserialization with {@link #doDeserialize(Object, byte, SchemaMetadata, Integer, Integer)} </li>
 * </ol>
 *
 * Extensions to this class may need to implement the above life cycle methods.
 *
 * @param <I> representation of the received input payload
 * @param <O> deserialized representation of the received payload
 * @param <S> parsed schema representation to be stored in local cache
 */
public abstract class AbstractSnapshotDeserializer<I, O, S> extends AbstractSerDes implements SnapshotDeserializer<I, O, Integer> {

    /**
     * Maximum inmemory cache size maintained in deserializer instance.
     */
    public static final String DESERIALIZER_SCHEMA_CACHE_MAX_SIZE = "schemaregistry.deserializer.schema.cache.size";

    /**
     * Default schema cache max size.
     */
    public static final Integer DEFAULT_SCHEMA_CACHE_SIZE = 1024;

    /**
     * Expiry interval(in milli seconds) after an access for an entry in schema cache
     */
    public static final String DESERIALIZER_SCHEMA_CACHE_EXPIRY_IN_SECS = "schemaregistry.deserializer.schema.cache.expiry.secs";

    /**
     * Default schema cache entry access expiration interval
     */
    public static final Long DEFAULT_DESERIALIZER_SCHEMA_CACHE_EXPIRY_IN_SECS = 60 * 5L;

    private LoadingCache<SchemaVersionKey, S> schemaCache;

    public AbstractSnapshotDeserializer() {
    }
    
    public AbstractSnapshotDeserializer(ISchemaRegistryClient schemaRegistryClient) {
        super(schemaRegistryClient);
    }

    @Override
    protected void doInit(Map<String, ?> config) {
        schemaCache = CacheBuilder.newBuilder()
                .maximumSize(getCacheMaxSize(config))
                .expireAfterAccess(getCacheExpiryInSecs(config), TimeUnit.SECONDS)
                .build(new CacheLoader<SchemaVersionKey, S>() {
                    @Override
                    public S load(SchemaVersionKey schemaVersionKey) {
                        try {
                            return getParsedSchema(schemaVersionKey);
                        } catch (SchemaNotFoundException | InvalidSchemaException e) {
                           throw new RegistryException(e);
                        }
                    }
                });
    }

    private Long getCacheExpiryInSecs(Map<String, ?> config) {
        Long value = getLongValue(config, DESERIALIZER_SCHEMA_CACHE_EXPIRY_IN_SECS, DEFAULT_DESERIALIZER_SCHEMA_CACHE_EXPIRY_IN_SECS);
        if (value < 0) {
            throw new IllegalArgumentException("Property: " + DESERIALIZER_SCHEMA_CACHE_EXPIRY_IN_SECS + " must be non negative.");
        }
        return value;
    }

    private Integer getCacheMaxSize(Map<String, ?> config) {
        Integer value = getIntegerValue(config, DESERIALIZER_SCHEMA_CACHE_MAX_SIZE, DEFAULT_SCHEMA_CACHE_SIZE);
        if (value < 0) {
            throw new IllegalArgumentException("Property: " + DESERIALIZER_SCHEMA_CACHE_MAX_SIZE + " must be non negative.");
        }
        return value;
    }

    protected Object getValue(Map<String, ?> config, String key, Object defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    protected Boolean getBooleanValue(Map<String, ?> config, String key, Boolean defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else {
            throw new IllegalArgumentException("Value for key: " + key + " is invalid");
        }
    }

    protected Integer getIntegerValue(Map<String, ?> config, String key, Integer defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else {
            throw new IllegalArgumentException("Value for key: " + key + " is invalid");
        }
    }

    protected Long getLongValue(Map<String, ?> config, String key, Long defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        } else {
            throw new IllegalArgumentException("Value for key: " + key + " is invalid");
        }
    }

    /**
     * Returns the parsed schema representation of the schema associated with the given {@code schemaVersionKey}
     * @param schemaVersionKey
     * @throws InvalidSchemaException when the associated schema is not valid.
     * @throws SchemaNotFoundException when there is no schema for the given {@code schemaVersionKey}
     */
    protected abstract S getParsedSchema(SchemaVersionKey schemaVersionKey) throws InvalidSchemaException, SchemaNotFoundException;

    @Override
    public O deserialize(I input,
                         Integer readerSchemaVersion) throws SerDesException {
        ensureInitialized();

        // it can be enhanced to have respective protocol handlers for different versions
        byte protocolId = retrieveProtocolId(input);
        SchemaIdVersion schemaIdVersion = retrieveSchemaIdVersion(protocolId, input);
        SchemaVersionInfo schemaVersionInfo;
        SchemaMetadata schemaMetadata;
        try {
            schemaVersionInfo = schemaRegistryClient.getSchemaVersionInfo(schemaIdVersion);
            schemaMetadata = schemaRegistryClient.getSchemaMetadataInfo(schemaVersionInfo.getName()).getSchemaMetadata();
        } catch (Exception e) {
            throw new RegistryException(e);
        }
        return doDeserialize(input, protocolId, schemaMetadata, schemaVersionInfo.getVersion(), readerSchemaVersion);

    }

    /**
     * Returns the deserialized object for the given input according to the schema information provided.
     *
     * @param input payload to be deserialized into.
     * @param protocolId protocol id for deserializtion.
     * @param schemaMetadata metadata about the schema
     * @param writerSchemaVersion schema version of writer used in building the serialized payload.
     * @param readerSchemaVersion schema version for reading/projection.
     * @throws SerDesException when any ser/des error occurs
     */
    protected abstract O doDeserialize(I input,
                                       byte protocolId,
                                       SchemaMetadata schemaMetadata,
                                       Integer writerSchemaVersion,
                                       Integer readerSchemaVersion) throws SerDesException;

    /**
     * Returns protocol id of ser/des.
     * @param input input from which protocol id can be retrieved
     * @throws SerDesException when any ser/des error occurs
     */
    protected abstract byte retrieveProtocolId(I input) throws SerDesException;

    /**
     * Retrieve the writer schema version and id from the given {@code input} for the given {@code protocolId}
     *
     * @param protocolId protocol id
     * @param input input from which version and id are to be retrieved
     * @throws SerDesException when any ser/des error occurs
     */
    protected abstract SchemaIdVersion retrieveSchemaIdVersion(byte protocolId, I input) throws SerDesException;

    /**
     * Returns Schema for the given {@code schemaVersionKey} from loadable cache.
     * @param schemaVersionKey schema version key
     */
    protected S getSchema(SchemaVersionKey schemaVersionKey) {
        try {
            return schemaCache.get(schemaVersionKey);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RegistryException) {
                throw (RegistryException) e.getCause();
            } else {
                throw new RegistryException(e);
            }
        }
    }

    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("init should be invoked before invoking deserialize operation");
        }

        if (closed) {
            throw new IllegalStateException("This deserializer is already closed");
        }
    }

}
