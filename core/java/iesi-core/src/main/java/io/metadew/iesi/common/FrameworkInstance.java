package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.repository.MetadataRepository;

public class FrameworkInstance {

    private static FrameworkInstance INSTANCE;

    public synchronized static FrameworkInstance getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkInstance();
        }
        return INSTANCE;
    }

    private FrameworkInstance() {
        FrameworkRuntime.getInstance().init();
    }


    public void shutdown() {
        for (MetadataRepository metadataRepository : MetadataRepositoryConfiguration.getInstance().getMetadataRepositories()) {
            if (metadataRepository != null) {
                metadataRepository.shutdown();
            }
        }
    }


}