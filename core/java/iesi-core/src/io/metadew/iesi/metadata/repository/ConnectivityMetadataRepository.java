package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.exception.*;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

public class ConnectivityMetadataRepository extends MetadataRepository {

    public ConnectivityMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
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
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("connection")) {
            Connection connection = objectMapper.convertValue(dataObject.getData(), Connection.class);
            save(connection, frameworkExecution);
        } else if (dataObject.getType().equalsIgnoreCase("environment")) {
            Environment environment = objectMapper.convertValue(dataObject.getData(), Environment.class);
            save(environment, frameworkExecution);
        } else if (dataObject.getType().equalsIgnoreCase("impersonation")) {
            Impersonation impersonation = objectMapper.convertValue(dataObject.getData(), Impersonation.class);
            save(impersonation, frameworkExecution);
        } else if (dataObject.getType().equalsIgnoreCase("repository")) {
            // TODO
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Connectivity repository is not responsible for loading saving {0}", dataObject.getType()), Level.TRACE);
        }
    }

    public void save(Connection connection, FrameworkExecution frameworkExecution) {
        frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting connection {0}-{1} into connectivity repository",
                connection.getName(), connection.getEnvironment()), Level.INFO);
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(frameworkExecution.getFrameworkInstance());
        try {
            connectionConfiguration.insertConnection(connection);
        } catch (ConnectionAlreadyExistsException e1) {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Connection {0}-{1} already exists in connectivity repository. Updating connection {0}-{1} instead.",
                    connection.getName(), connection.getEnvironment()), Level.DEBUG);
            try {
                connectionConfiguration.updateConnection(connection);
            } catch (ConnectionDoesNotExistException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void save(Environment environment, FrameworkExecution frameworkExecution) {
        frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting environment {0} into connectivity repository",
                environment.getName()), Level.INFO);
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(frameworkExecution.getFrameworkInstance());
        try {
            environmentConfiguration.insertEnvironment(environment);
        } catch (EnvironmentAlreadyExistsException e) {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Environment {0} already exists in connectivity repository. Updating connection {0} instead.",
                    environment.getName()), Level.DEBUG);
            try {
                environmentConfiguration.updateEnvironment(environment);
            } catch (EnvironmentDoesNotExistException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void save(Impersonation impersonation, FrameworkExecution frameworkExecution) {
        frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting impersonation {0} into connectivity repository",
                impersonation.getName()), Level.INFO);
        ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(frameworkExecution.getFrameworkInstance());
        try {
            impersonationConfiguration.insertImpersonation(impersonation);
        } catch (ImpersonationAlreadyExistsException e) {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Impersonation {0} already exists in connectivity repository. Updating impersonation {0} instead.",
                    impersonation.getName()), Level.DEBUG);
            try {
                impersonationConfiguration.updateImpersonation(impersonation);
            } catch (ImpersonationDoesNotExistException e1) {
                e1.printStackTrace();
            }
        }
    }

}
