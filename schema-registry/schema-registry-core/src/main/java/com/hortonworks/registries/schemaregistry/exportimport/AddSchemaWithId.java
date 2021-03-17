/**
 * Copyright 2016-2021 Cloudera, Inc.
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
 **/
package com.hortonworks.registries.schemaregistry.exportimport;

import com.hortonworks.registries.schemaregistry.SchemaIdVersion;
import com.hortonworks.registries.schemaregistry.SchemaMetadata;
import com.hortonworks.registries.schemaregistry.SchemaVersion;
import com.hortonworks.registries.schemaregistry.errors.IncompatibleSchemaException;
import com.hortonworks.registries.schemaregistry.errors.InvalidSchemaException;
import com.hortonworks.registries.schemaregistry.errors.SchemaBranchNotFoundException;
import com.hortonworks.registries.schemaregistry.errors.SchemaNotFoundException;

public interface AddSchemaWithId {

    /**
     * Add a new schema metadata and use the provided id for insertion. Note that calling this method
     * opens up two possibilities for failure: if the schema with the id already exists, <i>or</i>
     * if the schema with the same name already exists.
     *
     * @param id                    provided id for the new schema metadata
     * @param schemaMetadata        metadata info
     */
    Long addSchemaMetadata(Long id, SchemaMetadata schemaMetadata);

    /**
     * Add a new version to an existing schema metadata. Use the provided id for the version.
     *
     * @param schemaMetadata        schema metadata
     * @param versionId             provided id for the new schema version
     * @param schemaVersion         version info
     */
    SchemaIdVersion addSchemaVersion(SchemaMetadata schemaMetadata, Long versionId,
                                     SchemaVersion schemaVersion)
            throws InvalidSchemaException, IncompatibleSchemaException, SchemaNotFoundException,
                SchemaBranchNotFoundException;

}