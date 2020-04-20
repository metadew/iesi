package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import java.util.List;
import java.util.Optional;

public class ResultDatasetConfiguration extends Configuration<Dataset, DatasetKey> {

    private static ResultDatasetConfiguration INSTANCE;

    public synchronized static ResultDatasetConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResultDatasetConfiguration();
        }
        return INSTANCE;
    }

    private ResultDatasetConfiguration() {
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