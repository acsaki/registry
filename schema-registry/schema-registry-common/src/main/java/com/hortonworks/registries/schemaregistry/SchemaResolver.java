/*
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
 */
package com.hortonworks.registries.schemaregistry;

import com.hortonworks.registries.schemaregistry.errors.InvalidSchemaException;
import com.hortonworks.registries.schemaregistry.errors.SchemaNotFoundException;

/**
 * This interface is defined for resolving schemas which have dependencies on other schemas. 
 * {@link com.hortonworks.registries.schemaregistry.errors.CyclicSchemaDependencyException}
 * is thrown when there are cyclic dependencies among set of schemas.
 */
public interface SchemaResolver {

    /**
     * @return Resolved effective schema of the given schema after resolving all the dependencies.
     *
     * @param schemaText text of the schema for which dependencies should be resolved.
     * @throws InvalidSchemaException  when the schema is semantically invalid or when there are cyclic dependencies.
     * @throws SchemaNotFoundException when any of the dependent schemas is not found.
     */
    String resolveSchema(String schemaText) throws InvalidSchemaException, SchemaNotFoundException;

    /**
     * @return Resolved effective schema of the given schema after resolving all the dependencies.
     *
     * @param schemaVersionKey {@link SchemaVersionKey} of a specific schema version for which dependencies should be resolved.
     * @throws InvalidSchemaException  when the schema is semantically invalid or when there are cyclic dependencies.
     * @throws SchemaNotFoundException when any of the dependent schemas is not found.
     */
    String resolveSchema(SchemaVersionKey schemaVersionKey) throws InvalidSchemaException, SchemaNotFoundException;
}
