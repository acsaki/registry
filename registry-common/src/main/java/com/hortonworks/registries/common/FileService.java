/**
 * Copyright 2016-2022 Cloudera, Inc.
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
package com.hortonworks.registries.common;

import java.io.IOException;
import java.io.InputStream;

/** Service to work with files and storage. */
public interface FileService {

    void initialize(FileStorageConfiguration config) throws IOException;

    String uploadInternal(InputStream inputStream, String name) throws IOException;

    InputStream downloadInternal(String name) throws IOException;

    boolean deleteInternal(String name) throws IOException;

    boolean existsInternal(String name) throws IOException;

    class NoopFileService implements FileService {

        @Override
        public void initialize(FileStorageConfiguration config) throws IOException { }

        @Override
        public String uploadInternal(InputStream inputStream, String name) throws IOException {
            return null;
        }

        @Override
        public InputStream downloadInternal(String name) throws IOException {
            return null;
        }

        @Override
        public boolean deleteInternal(String name) throws IOException {
            return false;
        }

        @Override
        public boolean existsInternal(String name) throws IOException {
            return false;
        }
    }
}
