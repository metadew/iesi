package io.metadew.iesi.datatypes.dataset;

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
    public Optional<Dataset> get(DatasetKey datasetKey) {
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
