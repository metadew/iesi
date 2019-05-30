package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

public class ResultMetadataRepository extends MetadataRepository {

    public ResultMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "ResultTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ResultObjects.json";
    }

    @Override
    public String getCategory() {
        return "result";
    }

    @Override
    public String getCategoryPrefix() {
        return "RES";
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("log")) {
            Script script = objectMapper.convertValue(dataObject.getData(), Script.class);
            ScriptConfiguration scriptConfiguration = new ScriptConfiguration(script,
                    frameworkExecution.getFrameworkInstance());
            executeUpdate(scriptConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("component")) {
            Component component = objectMapper.convertValue(dataObject.getData(), Component.class);
            ComponentConfiguration componentConfiguration = new ComponentConfiguration(component,
                    frameworkExecution.getFrameworkInstance());
            executeUpdate(componentConfiguration.getInsertStatement());
        } else if (dataObject.getType().equalsIgnoreCase("subroutine")) {
            // TODO
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Result repository is not responsible for loading saving {0}", dataObject.getType()), Level.TRACE);
        }
    }
}
