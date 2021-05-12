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
package com.hortonworks.registries.storage.impl.jdbc.provider.postgresql.query;

import com.hortonworks.registries.storage.StorableKey;
import com.hortonworks.registries.storage.impl.jdbc.provider.sql.query.AbstractStorableKeyQuery;

/**
 *
 */
public class PostgresqlDeleteQuery extends AbstractStorableKeyQuery {

    public PostgresqlDeleteQuery(String nameSpace) {
        super(nameSpace);
    }

    public PostgresqlDeleteQuery(StorableKey storableKey) {
        super(storableKey);
    }

    @Override
    protected String createParameterizedSql() {
        String sql = "DELETE FROM  \"" + tableName + "\" WHERE " + join(getColumnNames(columns, "\"%s\" = ?"), " AND ");
        LOG.debug(sql);
        return sql;
    }
}