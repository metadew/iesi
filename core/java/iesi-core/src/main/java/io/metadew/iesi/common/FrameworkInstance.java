package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class FrameworkInstance {

    private static FrameworkInstance INSTANCE;

    public synchronized static FrameworkInstance getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkInstance();
        }
        return INSTANCE;
    }

    private FrameworkInstance() throws IOException {
        FrameworkRuntime.getInstance().init();
    }


    public void shutdown() throws Exception {
        log.debug("closing framework instance");
        for (MetadataRepository metadataRepository : MetadataRepositoryConfiguration.getInstance().getMetadataRepositories()) {
            if (metadataRepository != null) {
                metadataRepository.shutdown();
            }
        }
    }


}