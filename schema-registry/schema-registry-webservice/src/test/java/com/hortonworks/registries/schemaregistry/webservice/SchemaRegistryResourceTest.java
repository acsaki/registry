/**
 * Copyright 2016-2019 Cloudera, Inc.
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
package com.hortonworks.registries.schemaregistry.webservice;

import com.cloudera.dim.atlas.events.AtlasEventLogger;
import com.hortonworks.registries.common.CollectionResponse;
import com.hortonworks.registries.common.util.HadoopPlugin;
import com.hortonworks.registries.schemaregistry.ISchemaRegistry;
import com.hortonworks.registries.schemaregistry.SchemaFieldQuery;
import com.hortonworks.registries.schemaregistry.SchemaVersionKey;
import com.hortonworks.registries.schemaregistry.authorizer.agent.AuthorizationAgent;
import com.hortonworks.registries.schemaregistry.authorizer.core.util.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SchemaRegistryResourceTest {

    private SchemaRegistryResource underTest;
    private ISchemaRegistry schemaRegistryMock = mock(ISchemaRegistry.class);
    private AuthorizationAgent authorizationAgentMock = mock(AuthorizationAgent.class);
    private AuthenticationUtils authenticationUtils = new AuthenticationUtils(mock(HadoopPlugin.class));
    private AtlasEventLogger atlasEventLogger;
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String ORDER = "_orderByFields";
    private static final String FIELD_NAMESPACE = "fieldNamespace";
    private static final String TYPE = "type";
    

    @BeforeEach
    public void setup() {
        atlasEventLogger = mock(AtlasEventLogger.class);
        ISchemaRegistry schemaRegistryMock = mock(ISchemaRegistry.class);
        underTest = new SchemaRegistryResource(schemaRegistryMock, null, null, null, null, null, atlasEventLogger, null);
    }
    
    @Test
    public void createFilterForTestNameDescOrder() {
        //given
        String name = "some name";
        String desc = "interesting desc";
        String order = "timestamp,a";
        Map<String, String> expected = new HashMap<>();
        expected.put(NAME, name);
        expected.put(DESCRIPTION, desc);
        expected.put(ORDER, order);
        
        //when
        Map<String, String> actual = underTest.createFilterForSchema(Optional.ofNullable(name), 
                Optional.ofNullable(desc), Optional.ofNullable(order), Optional.ofNullable(null), 
                Optional.ofNullable(null), Optional.ofNullable(null), Optional.ofNullable(null), 
                Optional.ofNullable(null), Optional.ofNullable(null));
        
        //then
        assertEquals(expected, actual);
    }

    @Test
    public void createFilterForTestAllQueries() {
        //given
        String name = "some name";
        String desc = "interesting desc";
        String order = "timestamp,a";
        String id = "425";
        String type = "avro";
        String schemaGroup = "kafka";
        String validationLevel = "ALL";
        String compatibility = "BACKWARD";
        String evolve = "1";
        Map<String, String> expected = new HashMap<>();
        expected.put(NAME, name);
        expected.put(DESCRIPTION, desc);
        expected.put(ORDER, order);
        expected.put("id", id);
        expected.put(TYPE, type);
        expected.put("schemaGroup", schemaGroup);
        expected.put("validationLevel", validationLevel);
        expected.put("compatibility", compatibility);
        expected.put("evolve", evolve);

        //when
        Map<String, String> actual = underTest.createFilterForSchema(Optional.ofNullable(name), Optional.ofNullable(desc), 
                Optional.ofNullable(order), Optional.ofNullable(id), Optional.ofNullable(type), 
                Optional.ofNullable(schemaGroup), Optional.ofNullable(validationLevel), 
                Optional.ofNullable(compatibility), Optional.ofNullable(evolve));

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void createFilterForTestNameDescType() {
        //given
        String name = "some name";
        String desc = "interesting desc";
        String order = null;
        String id = null;
        String type = "avro";
        String schemaGroup = null;
        String validationLevel = null;
        String compatibility = null;
        String evolve = null;
        Map<String, String> expected = new HashMap<>();
        expected.put(NAME, name);
        expected.put(DESCRIPTION, desc);
        expected.put(TYPE, type);

        //when
        Map<String, String> actual = underTest.createFilterForSchema(Optional.ofNullable(name), Optional.ofNullable(desc), 
                Optional.ofNullable(order), Optional.ofNullable(id), Optional.ofNullable(type), 
                Optional.ofNullable(schemaGroup), Optional.ofNullable(validationLevel), 
                Optional.ofNullable(compatibility), Optional.ofNullable(evolve));

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void createFilterForNamespaceTestNameGroupType() {
        //given
        String name = "unique name";
        String namespace = "namespace";
        String type = "JSON";
        Map<String, String> expected = new HashMap<>();
        expected.put(NAME, name);
        expected.put(FIELD_NAMESPACE, namespace);
        expected.put(TYPE, type);
        
        //when
        Map<String, String> actual = underTest.createFilterForNamespace(Optional.ofNullable(name), Optional.ofNullable(namespace), Optional.ofNullable(type));
        
        //then
        assertEquals(expected, actual);

    }

    @Test
    public void createFilterForNamespaceTestNameGroup() {
        //given
        String name = "unique name";
        String namespace = "namespace";
        String type = null;
        Map<String, String> expected = new HashMap<>();
        expected.put(NAME, name);
        expected.put(FIELD_NAMESPACE, namespace);

        //when
        Map<String, String> actual = underTest.createFilterForNamespace(Optional.ofNullable(name), Optional.ofNullable(namespace), Optional.ofNullable(type));

        //then
        assertEquals(expected, actual);

    }

    @Test
    public void createFilterForNamespaceTestNameType() {
        //given
        String name = "unique name";
        String namespace = null;
        String type = "JSON";
        Map<String, String> expected = new HashMap<>();
        expected.put(NAME, name);
        expected.put(TYPE, type);

        //when
        Map<String, String> actual = underTest.createFilterForNamespace(Optional.ofNullable(name), Optional.ofNullable(namespace), Optional.ofNullable(type));

        //then
        assertEquals(expected, actual);

    }

    @Test
    public void createFilterForNamespaceTestGroupType() {
        //given
        String name = null;
        String namespace = "namespace";
        String type = "JSON";
        Map<String, String> expected = new HashMap<>();
        expected.put(FIELD_NAMESPACE, namespace);
        expected.put(TYPE, type);

        //when
        Map<String, String> actual = underTest.createFilterForNamespace(Optional.ofNullable(name), Optional.ofNullable(namespace), Optional.ofNullable(type));

        //then
        assertEquals(expected, actual);

    }

    @Test
    public void createFilterForNamespaceTestName() {
        //given
        String name = "unique name";
        String namespace = null;
        String type = null;
        Map<String, String> expected = new HashMap<>();
        expected.put(NAME, name);

        //when
        Map<String, String> actual = underTest.createFilterForNamespace(Optional.ofNullable(name), Optional.ofNullable(namespace), Optional.ofNullable(type));

        //then
        assertEquals(expected, actual);

    }
    
    @Test
    public void findSchemaByFieldsAllParametersProvided() throws Exception {
        //given
        Collection<SchemaVersionKey> schemaversions = new ArrayList<>();
        schemaversions.add(new SchemaVersionKey("test", 1));
        when(authorizationAgentMock.authorizeFindSchemasByFields(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(schemaversions);
        String name = "name";
        String namespace = "namespace";
        String type = "type";
        SecurityContext securityContext = mock(SecurityContext.class);
        SchemaFieldQuery schemaFieldQuery = new SchemaFieldQuery("test", "testnamespace", TYPE);
        underTest = new SchemaRegistryResource(schemaRegistryMock, authorizationAgentMock, authenticationUtils, null, null, null, atlasEventLogger, null) {
            @Override
            SchemaFieldQuery buildSchemaFieldQuery(MultivaluedMap<String, String> queryParameters) {
                MultivaluedMap<String, String> expectedParameters = new MultivaluedHashMap<>();
                expectedParameters.add(NAME, "name");
                expectedParameters.add(FIELD_NAMESPACE, "namespace");
                expectedParameters.add(TYPE, "type");
                assertEquals(expectedParameters, queryParameters);
                return schemaFieldQuery;
            }
        };
        when(schemaRegistryMock.findSchemasByFields(ArgumentMatchers.any())).thenReturn(schemaversions);
        
        //when
        Response actual = underTest.findSchemasByFields(name, namespace, type, securityContext);
        
        //then
        verify(schemaRegistryMock).findSchemasByFields(schemaFieldQuery);
        verify(authorizationAgentMock).authorizeFindSchemasByFields(null, schemaRegistryMock, schemaversions);
        CollectionResponse expectedEntity = CollectionResponse.newResponse().entities(schemaversions).build();
        assertEquals(200, actual.getStatus());
        assertEquals(expectedEntity.getEntities(), ((CollectionResponse) (actual.getEntity())).getEntities());

    }
    
    @Test
    public void buildSchemaFieldQueryTestNameNamespaceType() {
        //given
        String name = "apple";
        String namespace = "pear";
        String type = "strawberry";
        MultivaluedMap<String, String> queryparameters = new MultivaluedHashMap<>();
        queryparameters.add(NAME, name);
        queryparameters.add(FIELD_NAMESPACE, namespace);
        queryparameters.add(TYPE, type);
        SchemaFieldQuery expected = new SchemaFieldQuery(name, namespace, type);
        
        //when
        SchemaFieldQuery actual = underTest.buildSchemaFieldQuery(queryparameters);
        
        //then
        assertEquals(expected.toQueryMap(), actual.toQueryMap());
    }

    @Test
    public void buildSchemaFieldQueryTestNameType() {
        //given
        String name = "apple";
        String namespace = "pear";
        MultivaluedMap<String, String> queryparameters = new MultivaluedHashMap<>();
        queryparameters.add(NAME, name);
        queryparameters.add(FIELD_NAMESPACE, namespace);
        SchemaFieldQuery expected = new SchemaFieldQuery(name, namespace, null);

        //when
        SchemaFieldQuery actual = underTest.buildSchemaFieldQuery(queryparameters);

        //then
        assertEquals(expected.toQueryMap(), actual.toQueryMap());
    }

    @Test
    public void buildSchemaFieldQueryTestNamespaceType() {
        //given
        String namespace = "pear";
        String type = "strawberry";
        MultivaluedMap<String, String> queryparameters = new MultivaluedHashMap<>();
        queryparameters.add(FIELD_NAMESPACE, namespace);
        queryparameters.add(TYPE, type);
        SchemaFieldQuery expected = new SchemaFieldQuery(null, namespace, type);

        //when
        SchemaFieldQuery actual = underTest.buildSchemaFieldQuery(queryparameters);

        //then
        assertEquals(expected.toQueryMap(), actual.toQueryMap());
    }

    @Test
    public void buildSchemaFieldQueryTestNameNamespace() {
        //given
        String name = "apple";
        String namespace = "pear";
        MultivaluedMap<String, String> queryparameters = new MultivaluedHashMap<>();
        queryparameters.add(NAME, name);
        queryparameters.add(FIELD_NAMESPACE, namespace);
        SchemaFieldQuery expected = new SchemaFieldQuery(name, namespace, null);

        //when
        SchemaFieldQuery actual = underTest.buildSchemaFieldQuery(queryparameters);

        //then
        assertEquals(expected.toQueryMap(), actual.toQueryMap());
    }

    @Test
    public void buildSchemaFieldQueryTestType() {
        //given
        String type = "strawberry";
        MultivaluedMap<String, String> queryparameters = new MultivaluedHashMap<>();
        queryparameters.add(TYPE, type);
        SchemaFieldQuery expected = new SchemaFieldQuery(null, null, type);

        //when
        SchemaFieldQuery actual = underTest.buildSchemaFieldQuery(queryparameters);

        //then
        assertEquals(expected.toQueryMap(), actual.toQueryMap());
    }

    @Test
    public void buildSchemaFieldQueryTestNamespace() {
        //given
        String namespace = "pear";
        MultivaluedMap<String, String> queryparameters = new MultivaluedHashMap<>();
        queryparameters.add(FIELD_NAMESPACE, namespace);
        SchemaFieldQuery expected = new SchemaFieldQuery(null, namespace, null);

        //when
        SchemaFieldQuery actual = underTest.buildSchemaFieldQuery(queryparameters);

        //then
        assertEquals(expected.toQueryMap(), actual.toQueryMap());
    }

    @Test
    public void buildSchemaFieldQueryTestName() {
        //given
        String name = "apple";
        MultivaluedMap<String, String> queryparameters = new MultivaluedHashMap<>();
        queryparameters.add(NAME, name);
        SchemaFieldQuery expected = new SchemaFieldQuery(name, null, null);

        //when
        SchemaFieldQuery actual = underTest.buildSchemaFieldQuery(queryparameters);

        //then
        assertEquals(expected.toQueryMap(), actual.toQueryMap());
    }
}
