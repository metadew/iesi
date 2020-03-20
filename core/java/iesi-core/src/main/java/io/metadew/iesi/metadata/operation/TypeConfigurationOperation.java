package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import io.metadew.iesi.framework.operation.FrameworkPluginOperation;

import java.io.File;
import java.text.MessageFormat;
public class TypeConfigurationOperation {

    public TypeConfigurationOperation() {

    }

    public static String getTypeConfigurationFile(String dataObjectType, String typeName) {
        return null;
    }

    public static String getMappingConfigurationFile(String dataObjectType, String mappingName) {
        String configurationObject = mappingName + ".json";
        String conf = FrameworkConfiguration.getInstance().getMandatoryFrameworkFolder("data.mapping").getAbsolutePath()
                + File.separator + configurationObject;
        if (!FileTools.exists(conf)) {
            throw new RuntimeException(MessageFormat.format("mapping.notfound=cannot find {0}", conf));
        }
        return conf;
    }


}