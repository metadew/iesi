package io.metadew.iesi.metadata_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata_repository.repository.Repository;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

public class ConnectivityMetadataRepository extends MetadataRepository {

    public ConnectivityMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository, String repositoryObjectsPath,  String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
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
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(connection,
                    frameworkExecution);
            executeUpdate(connectionConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("environment")) {
            Environment environment = objectMapper.convertValue(dataObject.getData(), Environment.class);
            EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment,
                    frameworkExecution);
            executeUpdate(environmentConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("impersonation")) {
            Impersonation impersonation = objectMapper.convertValue(dataObject.getData(), Impersonation.class);
            ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, frameworkExecution);
            executeUpdate(impersonationConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("repository")) {
            // TODO
        } else 	{
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("This repository is not responsible for loading saving {0}", dataObject.getType()), Level.WARN);
        }
    }
}
