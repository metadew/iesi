package io.metadew.iesi.metadata_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.metadata_repository.repository.Repository;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

public class ControlMetadataRepository extends MetadataRepository {

    public ControlMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository, String repositoryObjectsPath,  String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
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
            UserConfiguration userConfiguration = new UserConfiguration(user,
                    frameworkExecution);
            executeUpdate(userConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("usergroup")) {
            //TODO
        } else if (dataObject.getType().equalsIgnoreCase("userrole")) {
            // TODO
        } else if (dataObject.getType().equalsIgnoreCase("spaceuser")) {
            // TODO
        } else 	{
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("This repository is not responsible for loading saving {0}", dataObject.getType()), Level.DEBUG);
        }
    }
}
