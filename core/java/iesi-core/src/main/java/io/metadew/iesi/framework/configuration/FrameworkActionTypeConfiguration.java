package io.metadew.iesi.framework.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;
import io.metadew.iesi.metadata.definition.ActionType;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FrameworkActionTypeConfiguration {

    private HashMap<String, ActionType> actionTypeMap;

    public FrameworkActionTypeConfiguration(FrameworkFolderConfiguration frameworkFolderConfiguration) {
        this.initalizeValues(frameworkFolderConfiguration);
    }

    private void initalizeValues(FrameworkFolderConfiguration frameworkFolderConfiguration) {
        this.setActionTypeMap(new HashMap<String, ActionType>());

        StringBuilder initFilePath = new StringBuilder();
        initFilePath.append(frameworkFolderConfiguration.getFolderAbsolutePath("metadata.conf"));
        initFilePath.append(File.separator);
        initFilePath.append("ActionTypes.json");

        DataObjectOperation dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(initFilePath.toString());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("actiontype")) {
                ActionType actionType = objectMapper.convertValue(dataObject.getData(), ActionType.class);
                this.getActionTypeMap().put(actionType.getName().toLowerCase(), actionType);
            }
        }
    }

    public void setActionTypesFromPlugins(FrameworkFolderConfiguration frameworkFolderConfiguration,
                                          List<FrameworkPluginConfiguration> frameworkPluginConfigurationList) {
        for (FrameworkPluginConfiguration frameworkPluginConfiguration : frameworkPluginConfigurationList) {
            StringBuilder initFilePath = new StringBuilder();
            initFilePath.append(frameworkPluginConfiguration.getFrameworkPlugin().getPath());
            initFilePath.append(frameworkFolderConfiguration.getFolderPath("metadata.conf"));
            initFilePath.append(File.separator);
            initFilePath.append("ActionTypes.json");
            String filePath = FilenameUtils.normalize(initFilePath.toString());

            if (FileTools.exists(filePath)) {
                DataObjectOperation dataObjectOperation = new DataObjectOperation();
                dataObjectOperation.setInputFile(filePath);
                dataObjectOperation.parseFile();
                ObjectMapper objectMapper = new ObjectMapper();
                for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
                    if (dataObject.getType().equalsIgnoreCase("actiontype")) {
                        ActionType actionType = objectMapper.convertValue(dataObject.getData(), ActionType.class);
                        if (this.getActionTypeMap().containsKey(actionType.getName().toLowerCase())) {
                            //System.out.println("item already present - skipping " + actionType.getName());
                            // TODO provide startup alert
                        } else {
                            this.getActionTypeMap().put(actionType.getName().toLowerCase(), actionType);
                        }
                    }
                }
            }
        }
    }

    // Create Getters and Setters
    public ActionType getActionType(String key) {
        return this.getActionTypeMap().get(key.toLowerCase());
    }

    public String getActionTypeClass(String key) {
        return this.getActionTypeMap().get(key.toLowerCase()).getClassName();
    }

    public HashMap<String, ActionType> getActionTypeMap() {
        return actionTypeMap;
    }

    public void setActionTypeMap(HashMap<String, ActionType> actionTypeMap) {
        this.actionTypeMap = actionTypeMap;
    }

}