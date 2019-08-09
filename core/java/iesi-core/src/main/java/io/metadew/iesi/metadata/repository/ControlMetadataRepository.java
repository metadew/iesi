package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.User;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ControlMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public ControlMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "ControlTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ControlObjects.json";
    }

    @Override
    public String getCategory() {
        return "control";
    }

    @Override
    public String getCategoryPrefix() {
        return "CTL";
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("user")) {
            User user = objectMapper.convertValue(dataObject.getData(), User.class);
            UserConfiguration userConfiguration = new UserConfiguration(FrameworkInstance.getInstance());
            userConfiguration.insertUser(user);
        } else if (dataObject.getType().equalsIgnoreCase("usergroup")) {
            //TODO
        } else if (dataObject.getType().equalsIgnoreCase("userrole")) {
            // TODO
        } else if (dataObject.getType().equalsIgnoreCase("spaceuser")) {
            // TODO
        } else {
            LOGGER.trace(MessageFormat.format("Control repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }
}
