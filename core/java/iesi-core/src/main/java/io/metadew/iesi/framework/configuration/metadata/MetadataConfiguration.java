package io.metadew.iesi.framework.configuration.metadata;

import io.metadew.iesi.framework.configuration.Configuration;
import io.metadew.iesi.framework.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import io.metadew.iesi.framework.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.framework.configuration.metadata.connectiontypes.MetadataConnectionTypesConfiguration;
import io.metadew.iesi.framework.configuration.metadata.objects.MetadataObjectsConfiguration;
import io.metadew.iesi.framework.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.framework.configuration.metadata.tables.MetadataTablesConfiguration;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class MetadataConfiguration {

    private static MetadataConfiguration INSTANCE;
    public static final String configurationKey = "metadata";

    public synchronized static MetadataConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataConfiguration();
        }
        return INSTANCE;
    }

    private MetadataConfiguration() {
        if (containsConfiguration()) {
            MetadataActionTypesConfiguration.getInstance();
            MetadataComponentTypesConfiguration.getInstance();
            MetadataConnectionTypesConfiguration.getInstance();
            MetadataTablesConfiguration.getInstance();
            MetadataObjectsConfiguration.getInstance();
            MetadataRepositoryConfiguration.getInstance();
        } else {
            log.warn("no metadata configuration found on system variable, classpath or filesystem");
        }
    }


    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) ||
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map);
    }

}
