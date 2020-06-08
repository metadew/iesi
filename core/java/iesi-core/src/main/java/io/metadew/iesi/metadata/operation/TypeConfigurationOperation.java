package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;


public class TypeConfigurationOperation {

    private static TypeConfigurationOperation INSTANCE;

    public synchronized static TypeConfigurationOperation getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TypeConfigurationOperation();
        }
        return INSTANCE;
    }

    private TypeConfigurationOperation() {

    }

    public String getMappingConfigurationFile(String dataObjectType, String mappingName) {
        String configurationObject = mappingName + ".json";
        Path conf = FrameworkConfiguration.getInstance().getMandatoryFrameworkFolder("data.mapping")
                .getAbsolutePath()
                .resolve(configurationObject);
        if (!Files.exists(conf)) {
            throw new RuntimeException(MessageFormat.format("mapping.notfound=cannot find {0}", conf));
        }
        return conf.toString();
    }


}