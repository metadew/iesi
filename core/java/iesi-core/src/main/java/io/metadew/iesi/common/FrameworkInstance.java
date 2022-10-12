package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Log4j2
@Component
public class FrameworkInstance {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public FrameworkInstance(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    public void shutdown() {
        log.debug("closing framework instance");
        for (MetadataRepository metadataRepository : metadataRepositoryConfiguration.getMetadataRepositories()) {
            if (metadataRepository != null) {
                metadataRepository.shutdown();
            }
        }
    }


}