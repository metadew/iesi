package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.operation.FrameworkPluginOperation;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.text.MessageFormat;

public class TypeConfigurationOperation {

    public TypeConfigurationOperation() {

    }

    public static String getTypeConfigurationFile(String dataObjectType, String typeName) {
        String configurationObject = dataObjectType + File.separator + typeName + ".json";
        String conf = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.conf") + File.separator + configurationObject;
        if (!FileTools.exists(conf)) {
            FrameworkPluginOperation frameworkPluginOperation = new FrameworkPluginOperation();
            if (frameworkPluginOperation.verifyPlugins(configurationObject)) {
                conf = frameworkPluginOperation.getPluginConfigurationFile();
            } else {
                throw new RuntimeException("action.type.notfound");
            }
        }
        return conf;
    }

    public static String getMappingConfigurationFile(String dataObjectType, String mappingName) {
        String configurationObject = mappingName + ".json";
        String conf = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("data.mapping")
                + File.separator + configurationObject;
        if (!FileTools.exists(conf)) {
            throw new RuntimeException(MessageFormat.format("mapping.notfound=cannot find {0}", conf));
        }
        return conf;
    }


}