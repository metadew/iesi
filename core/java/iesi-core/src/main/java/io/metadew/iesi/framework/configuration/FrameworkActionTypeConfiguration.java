package io.metadew.iesi.framework.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.definition.FrameworkPlugin;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.action.type.ActionType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameworkActionTypeConfiguration {

    private Map<String, ActionType> actionTypeMap;

    private static final Logger LOGGER = LogManager.getLogger();

    private static FrameworkActionTypeConfiguration INSTANCE;

    public synchronized static FrameworkActionTypeConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkActionTypeConfiguration();
        }
        return INSTANCE;
    }

    private FrameworkActionTypeConfiguration() {}

    public void init(FrameworkFolderConfiguration frameworkFolderConfiguration) {
        actionTypeMap = new HashMap<>();
        Path initFilePath = Paths.get(frameworkFolderConfiguration.getFolderAbsolutePath("metadata.conf"))
                .resolve("ActionTypes.json");
        DataObjectOperation dataObjectOperation = new DataObjectOperation(initFilePath);
        dataObjectOperation.parseFile();

        ObjectMapper objectMapper = new ObjectMapper();
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("actiontype")) {
                ActionType actionType = objectMapper.convertValue(dataObject.getData(), ActionType.class);
                actionTypeMap.put(actionType.getName().toLowerCase(), actionType);
            }
        }
    }

    public void setActionTypesFromPlugins(List<FrameworkPlugin> frameworkPlugins) {
        for (FrameworkPlugin frameworkPlugin : frameworkPlugins) {
            String initFilePath = frameworkPlugin.getPath() +
                    FrameworkFolderConfiguration.getInstance().getFolderPath("metadata.conf") +
                    File.separator +
                    "ActionTypes.json";
            String filePath = FilenameUtils.normalize(initFilePath);

            if (FileTools.exists(filePath)) {
                DataObjectOperation dataObjectOperation = new DataObjectOperation(filePath);
                dataObjectOperation.parseFile();
                ObjectMapper objectMapper = new ObjectMapper();
                for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
                    if (dataObject.getType().equalsIgnoreCase("actiontype")) {
                        ActionType actionType = objectMapper.convertValue(dataObject.getData(), ActionType.class);
                        if (this.getActionTypeMap().containsKey(actionType.getName().toLowerCase())) {
                            LOGGER.warn("item already present - skipping " + actionType.getName());
                        } else {
                            actionTypeMap.put(actionType.getName().toLowerCase(), actionType);
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

    public Map<String, ActionType> getActionTypeMap() {
        return actionTypeMap;
    }

    public void setActionTypeMap(HashMap<String, ActionType> actionTypeMap) {
        this.actionTypeMap = actionTypeMap;
    }

}