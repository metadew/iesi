package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.operation.FrameworkPluginOperation;

import java.io.File;

public class TypeConfigurationOperation {

    public TypeConfigurationOperation() {

    }

    public static String getTypeConfigurationFile(FrameworkExecution frameworkExecution, String dataObjectType,
                                                  String typeName) {
        String configurationObject = dataObjectType + File.separator + typeName + ".json";
        String conf = frameworkExecution.getFrameworkConfiguration().getFolderConfiguration()
                .getFolderAbsolutePath("metadata.conf") + File.separator + configurationObject;

        if (!FileTools.exists(conf)) {
            FrameworkPluginOperation frameworkPluginOperation = new FrameworkPluginOperation(frameworkExecution);
            if (frameworkPluginOperation.verifyPlugins(configurationObject)) {
                conf = frameworkPluginOperation.getPluginConfigurationFile();
            } else {
                throw new RuntimeException("action.type.notfound");
            }
        }
        return conf;
    }

    public static String getMappingConfigurationFile(FrameworkExecution frameworkExecution, String dataObjectType, String mappingName) {
        String configurationObject = mappingName + ".json";
        String conf = frameworkExecution.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("data.mapping")
                + File.separator + configurationObject;
        if (!FileTools.exists(conf)) {
            throw new RuntimeException("mapping.notfound");
        }
        return conf;
    }


}