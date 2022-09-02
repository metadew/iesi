package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

@Component
public class TypeConfigurationOperation {

    public String getMappingConfigurationFile(String dataObjectType, String mappingName) {
        String configurationObject = mappingName + ".json";
        Path conf = SpringContext.getBean(FrameworkConfiguration.class).getMandatoryFrameworkFolder("data.mapping")
                .getAbsolutePath()
                .resolve(configurationObject);
        if (!Files.exists(conf)) {
            throw new RuntimeException(MessageFormat.format("mapping.notfound=cannot find {0}", conf));
        }
        return conf.toString();
    }


}