package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ConnectivityMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    private final EnvironmentConfiguration environmentConfiguration = SpringContext.getBean(EnvironmentConfiguration.class);
    private final ImpersonationConfiguration impersonationConfiguration = SpringContext.getBean(ImpersonationConfiguration.class);
    private final ConnectionConfiguration connectionConfiguration = SpringContext.getBean(ConnectionConfiguration.class);
    private final SecurityGroupService securityGroupService = SpringContext.getBean(SecurityGroupService.class);

    public ConnectivityMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "connectivity";
    }

    @Override
    public void save(DataObject dataObject) {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("connection")) {
            Connection connection = (Connection) objectMapper.convertValue(dataObject, Metadata.class);
            save(connection);
        } else if (dataObject.getType().equalsIgnoreCase("environment")) {
            Environment environment = (Environment) objectMapper.convertValue(dataObject, Metadata.class);
            save(environment);
        } else if (dataObject.getType().equalsIgnoreCase("impersonation")) {
            Impersonation impersonation = (Impersonation) objectMapper.convertValue(dataObject, Metadata.class);
            save(impersonation);
        } else if (dataObject.getType().equalsIgnoreCase("repository")) {
            // TODO
        } else {
            LOGGER.trace(MessageFormat.format("Connectivity repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(Connection connection) {
        LOGGER.info(MessageFormat.format("Inserting connection {0}-{1} into connectivity repository",
                connection.getMetadataKey().getName(), connection.getMetadataKey().getEnvironmentKey().getName()));
        try {
            if (connection.getSecurityGroupKey() == null) {
                LOGGER.warn("{0} not linked to a security group, linking it to the public security group");
                SecurityGroup publicSecurityGroup = securityGroupService.get("PUBLIC")
                        .orElseThrow(() -> new RuntimeException("Could not find security group with name PUBLIC"));
                connection.setSecurityGroupKey(publicSecurityGroup.getMetadataKey());
                connection.setSecurityGroupName(publicSecurityGroup.getName());
            }
            connectionConfiguration.insert(connection);
        } catch (MetadataAlreadyExistsException e1) {
            LOGGER.info(MessageFormat.format("Connection {0}-{1} already exists in connectivity repository. Updating connection {0}-{1} instead.",
                    connection.getMetadataKey().getName(), connection.getMetadataKey().getEnvironmentKey().getName()));
            connectionConfiguration.update(connection);
        }
    }

    public void save(Environment environment) {
        LOGGER.info(MessageFormat.format("Inserting environment {0} into connectivity repository",
                environment.getName()));
        try {
            environmentConfiguration.insert(environment);
        } catch (MetadataAlreadyExistsException e) {
            LOGGER.info(MessageFormat.format("Environment {0} already exists in connectivity repository. Updating connection {0} instead.",
                    environment.getName()));
            environmentConfiguration.update(environment);
        }
    }

    public void save(Impersonation impersonation) {
        LOGGER.info(MessageFormat.format("Inserting impersonation {0} into connectivity repository",
                impersonation.getMetadataKey().getName()));
        try {
            impersonationConfiguration.insertImpersonation(impersonation);
        } catch (MetadataAlreadyExistsException e) {
            LOGGER.info(MessageFormat.format("Impersonation {0} already exists in connectivity repository. Updating impersonation {0} instead.",
                    impersonation.getMetadataKey().getName()));
            impersonationConfiguration.updateImpersonation(impersonation);
        }
    }

}
