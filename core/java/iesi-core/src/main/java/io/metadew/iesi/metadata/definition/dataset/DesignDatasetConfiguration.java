package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import java.util.List;
import java.util.Optional;

public class DesignDatasetConfiguration extends Configuration<Dataset, DatasetKey> {

    private static DesignDatasetConfiguration INSTANCE;

    public synchronized static DesignDatasetConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DesignDatasetConfiguration();
        }
        return INSTANCE;
    }

    private DesignDatasetConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<Dataset> get(DatasetKey metadataKey) {
        return Optional.empty();
    }

    @Override
    public List<Dataset> getAll() {
        return null;
    }

    @Override
    public void delete(DatasetKey metadataKey) {

    }

    @Override
    public void insert(Dataset metadata) {

    }

}