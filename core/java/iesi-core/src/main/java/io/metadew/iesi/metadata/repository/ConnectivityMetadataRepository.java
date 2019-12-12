package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.*;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class ConnectivityMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public ConnectivityMetadataRepository(String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, scope, instanceName, repositoryCoordinator);
        ConnectionConfiguration.getInstance().init(this);
        EnvironmentConfiguration.getInstance().init(this);
        ImpersonationConfiguration.getInstance().init(this);
    }

    public ConnectivityMetadataRepository(String name, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, instanceName, repositoryCoordinator);
        ConnectionConfiguration.getInstance().init(this);
        EnvironmentConfiguration.getInstance().init(this);
        ImpersonationConfiguration.getInstance().init(this);
    }

    public ConnectivityMetadataRepository(String tablePrefix, RepositoryCoordinator repositoryCoordinator, String name, String scope,
                                          List<MetadataObject> metadataObjects, List<MetadataTable> metadataTables) {
        super(tablePrefix, repositoryCoordinator, name, scope, metadataObjects, metadataTables);
        ConnectionConfiguration.getInstance().init(this);
        EnvironmentConfiguration.getInstance().init(this);
        ImpersonationConfiguration.getInstance().init(this);
    }

    @Override
    public String getDefinitionFileName() {
        return "ConnectivityTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ConnectivityObjects.json";
    }

    @Override
    public String getCategory() {
        return "connectivity";
    }

    @Override
    public String getCategoryPrefix() {
        return "CXN";
    }

    @Override
    public void save(DataObject dataObject) {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("connection")) {
            Connection connection = objectMapper.convertValue(dataObject.getData(), Connection.class);
            save(connection);
        } else if (dataObject.getType().equalsIgnoreCase("environment")) {
            Environment environment = objectMapper.convertValue(dataObject.getData(), Environment.class);
            save(environment);
        } else if (dataObject.getType().equalsIgnoreCase("impersonation")) {
            Impersonation impersonation = objectMapper.convertValue(dataObject.getData(), Impersonation.class);
            save(impersonation);
        } else if (dataObject.getType().equalsIgnoreCase("repository")) {
            // TODO
        } else {
            LOGGER.trace(MessageFormat.format("Connectivity repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(Connection connection) {
        LOGGER.info(MessageFormat.format("Inserting connection {0}-{1} into connectivity repository",
                connection.getName(), connection.getEnvironment()));
        try {
            ConnectionConfiguration.getInstance().insert(connection);
        } catch (MetadataAlreadyExistsException e1) {
            LOGGER.info(MessageFormat.format("Connection {0}-{1} already exists in connectivity repository. Updating connection {0}-{1} instead.",
                    connection.getName(), connection.getEnvironment()));
            try {
                ConnectionConfiguration.getInstance().update(connection);
            } catch (MetadataDoesNotExistException e2) {
                StringWriter stackTrace = new StringWriter();
                e2.printStackTrace(new PrintWriter(stackTrace));
                LOGGER.warn("exeption=" + e2.getMessage());
                LOGGER.info("exception.stacktrace=" + stackTrace.toString());
            }
        }
    }

    public void save(Environment environment) {
        LOGGER.info(MessageFormat.format("Inserting environment {0} into connectivity repository",
                environment.getName()));
        try {
            EnvironmentConfiguration.getInstance().insertEnvironment(environment);
        } catch (EnvironmentAlreadyExistsException e) {
            LOGGER.info(MessageFormat.format("Environment {0} already exists in connectivity repository. Updating connection {0} instead.",
                    environment.getName()));
            try {
                EnvironmentConfiguration.getInstance().updateEnvironment(environment);
            } catch (EnvironmentDoesNotExistException e1) {
                StringWriter stackTrace = new StringWriter();
                e1.printStackTrace(new PrintWriter(stackTrace));
                LOGGER.warn("exeption=" + e1.getMessage());
                LOGGER.info("exception.stacktrace=" + stackTrace.toString());
            }
        }
    }

    public void save(Impersonation impersonation) {
        LOGGER.info(MessageFormat.format("Inserting impersonation {0} into connectivity repository",
                impersonation.getName()));
        try {
            ImpersonationConfiguration.getInstance().insertImpersonation(impersonation);
        } catch (ImpersonationAlreadyExistsException e) {
            LOGGER.info(MessageFormat.format("Impersonation {0} already exists in connectivity repository. Updating impersonation {0} instead.",
                    impersonation.getName()));
            try {
                ImpersonationConfiguration.getInstance().updateImpersonation(impersonation);
            } catch (ImpersonationDoesNotExistException e1) {
                StringWriter stackTrace = new StringWriter();
                e1.printStackTrace(new PrintWriter(stackTrace));
                LOGGER.warn("exeption=" + e1.getMessage());
                LOGGER.info("exception.stacktrace=" + stackTrace.toString());
            }
        }
    }

}
