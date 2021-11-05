package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

import java.util.List;
import java.util.Optional;

public class DatasetService implements IDatasetService {
    private static DatasetService INSTANCE;

    public synchronized static DatasetService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatasetService();
        }
        return INSTANCE;
    }

    private DatasetService() {
    }


    @Override
    public boolean exists(DatasetKey datasetKey) {
        return DatasetConfiguration.getInstance().exists(datasetKey);
    }

    @Override
    public boolean exists(String name) {
        return DatasetConfiguration.getInstance().existsByName(name);
    }

    @Override
    public boolean getIdByName(String name) {
        return DatasetConfiguration.getInstance().existsByName(name);
    }

    @Override
    public Optional<Dataset> get(DatasetKey datasetKey) {
        if (exists(datasetKey)) {
            throw new MetadataDoesNotExistException(datasetKey);
        }
        return DatasetConfiguration.getInstance().get(datasetKey);
    }

    @Override
    public List<Dataset> getAll() {
        return DatasetConfiguration.getInstance().getAll();
    }

    @Override
    public Optional<Dataset> getByName(String name) {
        return DatasetConfiguration.getInstance().getByName(name);
    }

    @Override
    public void create(Dataset dataset) {
        DatasetConfiguration.getInstance().insert(dataset);
    }

    @Override
    public void delete(Dataset dataset) {
        delete(dataset.getMetadataKey());
    }

    @Override
    public void delete(DatasetKey datasetKey) {
        DatasetConfiguration.getInstance().delete(datasetKey);
    }

    @Override
    public void update(Dataset dataset) {
        DatasetConfiguration.getInstance().update(dataset);
    }
}
