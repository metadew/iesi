package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ControlMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public ControlMetadataRepository(String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, scope, instanceName, repositoryCoordinator);
    }

    public ControlMetadataRepository(String name, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, instanceName, repositoryCoordinator);
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
    public void save(DataObject dataObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("user")) {
            User user = objectMapper.convertValue(dataObject.getData(), User.class);
            UserConfiguration userConfiguration = new UserConfiguration();
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
