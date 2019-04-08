package io.metadew.iesi.metadata_repository.repository.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.metadata_repository.repository.Repository;
import io.metadew.iesi.metadata_repository.repository.database.Database;
import io.metadew.iesi.metadata_repository.repository.database.OracleDatabase;
import io.metadew.iesi.metadata_repository.repository.database.connection.OracleDatabaseConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OracleRepositoryConfiguration extends RepositoryConfiguration {
    private final String jdbcConnectionStringServiceFormat = "jdbc:netezza:thin::@//%s:%s/%s";
    private final String jdbcConnectionStringTnsAliasFormat = "jdbc:oracle:thin:%s:%s:%s";

    private String jdbcConnectionString;
    private String host;
    private String port;
    private String name;
    private String service;
    private String tnsAlias;
    private String schema;
    private String schemaUser;
    private String schemaUserPassword;
    private String writerUser;
    private String writerUserPassword;
    private String readerUser;
    private String readerUserPassword;


    public OracleRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
       super(configFile, frameworkSettingConfiguration);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
        // schema
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema").get()).isPresent()) {
            schema = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema").get()).get();
        }
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.name").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.name").get()).isPresent()) {
            name = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.name").get()).get();
        }
        // set users and passwords
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.user").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.user").get()).isPresent()) {
            schemaUser = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.user").get()).get();
        }
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.user.password").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.user.password").get()).isPresent()) {
            schemaUserPassword = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.user.password").get()).get();
        }
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.writer").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.writer").get()).isPresent()) {
            writerUser = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.writer").get()).get();
        }
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.writer.password").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.writer.password").get()).isPresent()) {
            writerUserPassword = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.writer.password").get()).get();
        }
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.reader").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.reader").get()).isPresent()) {
            readerUser = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.reader").get()).get();
        }
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.reader.password").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.reader.password").get()).isPresent()) {
            readerUserPassword = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.schema.reader.password").get()).get();
        }

        // get jdbc connection url
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.connection.string").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.connection.string").get()).isPresent()) {
            jdbcConnectionString = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.connection.string").get()).get();
        } else if ((frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.host").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.host").get()).isPresent()) &&
                (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.port").isPresent() &&
                        configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.port").get()).isPresent()) &&
                (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.service").isPresent() &&
                        configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.service").get()).isPresent())) {
            host = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.host").get()).get();
            port = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.port").get()).get();
            service = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.service").get()).get();
            jdbcConnectionString = String.format(jdbcConnectionStringServiceFormat, host, port, service);
        } else if ((frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.host").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.host").get()).isPresent()) &&
                (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.port").isPresent() &&
                        configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.port").get()).isPresent()) &&
                (frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.tnsalias").isPresent() &&
                        configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.tnsalias").get()).isPresent())){
            host = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.host").get()).get();
            port = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.port").get()).get();
            tnsAlias = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.oracle.service").get()).get();
            jdbcConnectionString = String.format(jdbcConnectionStringTnsAliasFormat, host, port, tnsAlias);}

        else {
            throw new RuntimeException("Could not initialize Oracle configuration. No connection string or host, port and name provided");
        }
    }

    @Override
    public Repository toRepository() {
        Map<String, Database> databases = new HashMap<>();

        getUser().ifPresent(owner -> {
            databases.put("owner", new OracleDatabase(new OracleDatabaseConnection(getJdbcConnectionString(), owner, getUserPassword().orElse("")), getSchema().orElse("")));
            databases.put("writer", new OracleDatabase(new OracleDatabaseConnection(getJdbcConnectionString(), owner, getUserPassword().orElse("")), getSchema().orElse("")));
            databases.put("reader", new OracleDatabase(new OracleDatabaseConnection(getJdbcConnectionString(), owner, getUserPassword().orElse("")), getSchema().orElse("")));
        });

        getWriter().ifPresent(writer -> {
            databases.put("writer", new OracleDatabase(new OracleDatabaseConnection(getJdbcConnectionString(), writer, getWriterPassword().orElse("")), getSchema().orElse("")));
            databases.put("reader", new OracleDatabase(new OracleDatabaseConnection(getJdbcConnectionString(), writer, getWriterPassword().orElse("")), getSchema().orElse("")));
        });

        getReader().ifPresent(reader -> {
            databases.put("reader", new OracleDatabase(new OracleDatabaseConnection(getJdbcConnectionString(), reader, getReaderPassword().orElse("")), getSchema().orElse("")));
        });
        return new Repository(databases);
    }

    public String getJdbcConnectionString() {
        return jdbcConnectionString;
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public Optional<String> getPort() {
        return Optional.ofNullable(port);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getService() {
        return Optional.ofNullable(service);
    }

    public Optional<String> getTnsAlias() {
        return Optional.ofNullable(tnsAlias);
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    public Optional<String> getUser() {
        return Optional.ofNullable(schemaUser);
    }

    public Optional<String> getUserPassword() {
        return Optional.ofNullable(schemaUserPassword);
    }

    public Optional<String> getWriter() {
        return Optional.ofNullable(writerUser);
    }

    public Optional<String> getWriterPassword() {
        return Optional.ofNullable(writerUserPassword);
    }

    public Optional<String> getReader() {
        return Optional.ofNullable(readerUser);
    }

    public Optional<String> getReaderPassword() {
        return Optional.ofNullable(readerUserPassword);
    }
}
