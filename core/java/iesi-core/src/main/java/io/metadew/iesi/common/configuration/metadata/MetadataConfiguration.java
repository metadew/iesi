package io.metadew.iesi.common.configuration.metadata;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.Map;

@Log4j2
@org.springframework.context.annotation.Configuration
public class MetadataConfiguration {

    public static final String configurationKey = "metadata";

    Configuration configuration = SpringContext.getBean(Configuration.class);



    @PostConstruct
    private void postConstruct() {
        if (containsConfiguration()) {
            //TODO: REMOVE THIS CLASS AND ADD @CONDITIONAL ON EVERY METADATA CONFIGURATION
        } else {
            log.warn("no metadata configuration found on system variable, classpath or filesystem");
        }
    }


    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map);
    }

}
