/**
 * Copyright 2017-2021 Cloudera, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hortonworks.registries.schemaregistry.webservice.validator.exception;

public class InvalidJarFileException extends Exception {

    private static final long serialVersionUID = -7688628446475079619L;

    public InvalidJarFileException(String message) {
        super(message);
    }

    public InvalidJarFileException(String message, Throwable cause) {
        super(message, cause);
    }
}