package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import java.util.List;
import java.util.Optional;

public class ResultDatasetLabelConfiguration extends Configuration<DatasetLabel, DatasetLabelKey> {

    private static ResultDatasetLabelConfiguration INSTANCE;

    public synchronized static ResultDatasetLabelConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResultDatasetLabelConfiguration();
        }
        return INSTANCE;
    }

    private ResultDatasetLabelConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<DatasetLabel> get(DatasetLabelKey metadataKey) {
        return Optional.empty();
    }

    @Override
    public List<DatasetLabel> getAll() {
        return null;
    }

    @Override
    public void delete(DatasetLabelKey metadataKey) {

    }

    @Override
    public void insert(DatasetLabel metadata) {

    }

}