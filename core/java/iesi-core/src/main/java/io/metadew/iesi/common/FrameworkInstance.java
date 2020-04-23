package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import java.io.IOException;

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


    public void shutdown() {
        for (MetadataRepository metadataRepository : MetadataRepositoryConfiguration.getInstance().getMetadataRepositories()) {
            if (metadataRepository != null) {
                metadataRepository.shutdown();
            }
        }
    }


}