package io.metadew.iesi.common.configuration.metadata;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import io.metadew.iesi.common.configuration.metadata.componenttypes.MetadataComponentTypesConfiguration;
import io.metadew.iesi.common.configuration.metadata.connectiontypes.MetadataConnectionTypesConfiguration;
import io.metadew.iesi.common.configuration.metadata.objects.MetadataObjectsConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class MetadataConfiguration {

    private static MetadataConfiguration INSTANCE;
    public static final String configurationKey = "metadata";

    Configuration configuration = SpringContext.getBean(Configuration.class);

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
            MetadataPolicyConfiguration.getInstance();
        } else {
            log.warn("no metadata configuration found on system variable, classpath or filesystem");
        }
    }


    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map);
    }

}
