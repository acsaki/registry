# Atlas configuration only applies if enabled is true
atlas:
  enabled: ${config.atlasConfiguration.enabled?c}
  atlasUrls:
    # list of Atlas URLs
    - http://<atlas host url>:31000
  basicAuth:
    username: "${config.atlasConfiguration.basicAuth.username}"
    password: "${config.atlasConfiguration.basicAuth.password}"


# registries configuration
modules:
  - name: schema-registry
    className: com.hortonworks.registries.schemaregistry.webservice.SchemaRegistryModule
    config:
      schemaProviders:
        - providerClass: "com.hortonworks.registries.schemaregistry.avro.AvroSchemaProvider"
          defaultSerializerClass: "com.hortonworks.registries.schemaregistry.serdes.avro.AvroSnapshotSerializer"
          defaultDeserializerClass: "com.hortonworks.registries.schemaregistry.serdes.avro.AvroSnapshotDeserializer"
          hashFunction: "MD5"
      # schema reviewer configuration
      customSchemaStateExecutor:
        className: "com.hortonworks.registries.schemaregistry.state.DefaultCustomSchemaStateExecutor"
        props:
      # authorization properties
      #authorization:
      #  authorizationAgentClassName: "com.hortonworks.registries.schemaregistry.authorizer.agent.DefaultAuthorizationAgent"
      # schema cache properties
      cache:
        enableCaching : false

servletFilters:
# - className: "com.hortonworks.registries.auth.server.AuthenticationFilter"
#   params:
#     type: "kerberos"
#     kerberos.principal: "HTTP/streamline-ui-host.com"
#     kerberos.keytab: "/vagrant/keytabs/http.keytab"
#     enable.trusted.proxy: true
#     proxyuser.knox.hosts: 172.22.64.70
#     login.enabled: "true"
#     spnego.enabled: "true"
#     kerberos.name.rules: "RULE:[2:$1@$0]([jt]t@.*EXAMPLE.COM)s/.*/$MAPRED_USER/ RULE:[2:$1@$0]([nd]n@.*EXAMPLE.COM)s/.*/$HDFS_USER/DEFAULT"
#     allowed.resources: "401.html,back-default.png,favicon.ico"
# Note that it is highly recommended to force ssl connections if you are using the jwt handler config below. Unsecured connections will expose jwt
# - className: "com.hortonworks.registries.auth.server.AuthenticationFilter"
#   params:
#     type: "com.hortonworks.registries.auth.server.JWTAuthenticationHandler"
#     allowed.resources: "401.html,back-default.png,favicon.ico"
#     authentication.provider.url: "https://localhost:8883/gateway/knoxsso/api/v1/websso"
#     public.key.pem: "<public key corresponding to the PKI key pair of the token issuer>"

 - className: "com.hortonworks.registries.schemaregistry.webservice.RewriteUriFilter"
   params:
     # value format is [<targetpath>,<paths-should-be-redirected-to>,*|]*
     # below /subjects and /schemas/ids are forwarded to /api/v1/confluent
     forwardPaths: "/api/v1/confluent,/subjects/*,/schemas/ids/*"
     redirectPaths: "/ui/,/"

## HA configuration
## When no configuration is set, then all the nodes in schema registry cluster are eligible to write to the backend storage.
#haConfig:
#  className: com.hortonworks.registries.ha.zk.ZKLeadershipParticipant
#  config:
#    # This url is a list of ZK servers separated by ,
#    connect.url: "localhost:2181"
#    # root node prefix in ZK for this instance
#    root: "/registry"
#    session.timeout.ms: 30000
#    connection.timeout.ms: 20000
#    retry.limit: 5
#    retry.base.sleep.time.ms: 1000
#    retry.max.sleep.time.ms: 5000

# Filesystem based jar storage
fileStorageConfiguration:
  className: "com.hortonworks.registries.common.util.LocalFileSystemStorage"
  properties:
    directory: "${config.fileStorageConfiguration.properties.directory}"


# MySQL based jdbc provider configuration is:
storageProviderConfiguration:
 providerClass: "com.hortonworks.registries.storage.impl.jdbc.JdbcStorageManager"
 properties:
   db.type: "${config.storageProviderConfiguration.properties.dbtype}"
   queryTimeoutInSecs: ${config.storageProviderConfiguration.properties.queryTimeoutInSecs}
   db.properties:
     dataSourceClassName: "${config.storageProviderConfiguration.properties.properties.dataSourceClassName}"
     dataSource.url: "${config.storageProviderConfiguration.properties.properties.dataSourceUrl}"
     dataSource.user: "${config.storageProviderConfiguration.properties.properties.dataSourceUser}"
     dataSource.password: "${config.storageProviderConfiguration.properties.properties.dataSourcePassword}"

#swagger configuration
swagger:
  resourcePackage: com.hortonworks.registries.schemaregistry.webservice,com.cloudera.dim.atlas

#enable CORS, may want to disable in production
enableCors: true
#Set below 3 properties if server needs a proxy to connect. Useful to download mysql jar
#httpProxyUrl: "http://proxyHost:port"
#httpProxyUsername: "username"
#httpProxyPassword: "password"

server:
  allowedMethods: 
    - GET
    - POST
    - PUT
    - DELETE
    - HEAD
    - OPTIONS
  applicationConnectors:
    - type: http
      port: ${port}
  adminConnectors: []

# Logging settings.
logging:
  level: INFO
  loggers:
    com.hortonworks.registries: DEBUG
    com.cloudera.dim: DEBUG

# Config for schema registry kerberos principle
#serviceAuthenticationConfiguration:
#  type: kerberos
#  properties:
#    principal: "schema-registry/hostname@GCE.CLOUDERA.COM"
#    keytab: "/tmp/schema-registry.keytab"
