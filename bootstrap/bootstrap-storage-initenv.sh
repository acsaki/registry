#!/usr/bin/env bash

#
# Copyright 2017-2019 Cloudera, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at

#   http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# Resolve links - $0 may be a softlink
PRG="${0}"

while [ -h "${PRG}" ]; do
  ls=`ls -ld "${PRG}"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "${PRG}"`/"$link"
  fi
done

BOOTSTRAP_DIR=`dirname ${PRG}`
CONFIG_FILE_PATH=${BOOTSTRAP_DIR}/../conf/registry.yaml

# Which java to use
if [ -z "${JAVA_HOME}" ]; then
  JAVA="java"
else
  JAVA="${JAVA_HOME}/bin/java"
fi

INITIALIZER_MAIN_CLASS=com.hortonworks.registries.storage.tool.sql.DatabaseUserInitializer
for file in "${BOOTSTRAP_DIR}"/lib/*.jar;
do
    CLASSPATH="$CLASSPATH":"$file"
done

printUsage() {
    cat <<-EOF
USAGE: $0 admin-jdbc-url admin-username admin-password target-username target-password target-database [config file path]

   config file path : optional, "${CONFIG_FILE_PATH}" used if omitted
EOF
}

if [ $# -lt 6 ]
then
    printUsage
    exit 1
fi

[[ $# -gt 6 ]] && CONFIG_FILE_PATH="${7}"

echo "Using Configuration file: ${CONFIG_FILE_PATH}"
${JAVA} -Dbootstrap.dir=$BOOTSTRAP_DIR -cp ${CLASSPATH} ${INITIALIZER_MAIN_CLASS} -c ${CONFIG_FILE_PATH} \
  --admin-jdbc-url ${1} --admin-username ${2} --admin-password ${3} --target-username ${4} --target-password ${5} --target-database ${6}
