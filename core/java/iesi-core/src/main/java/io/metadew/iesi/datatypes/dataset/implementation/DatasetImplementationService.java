package io.metadew.iesi.datatypes.dataset.implementation;

import java.util.List;
import java.util.Optional;

public abstract class DatasetImplementationService<T extends DatasetImplementation> implements IDatasetImplementationService<T> {

    @Override
    public Optional<DatasetImplementation> get(DatasetImplementationKey datasetImplementationKey) {
        return DatasetImplementationConfiguration.getInstance()
                .get(datasetImplementationKey);
    }

    @Override
    public void create(T datasetImplementation) {
        DatasetImplementationConfiguration.getInstance().insert(datasetImplementation);
    }

    @Override
    public void delete(T datasetImplementation) {
        delete(datasetImplementation.getMetadataKey());
    }

    @Override
    public void delete(DatasetImplementationKey datasetImplementationKey) {
        DatasetImplementationConfiguration.getInstance().delete(datasetImplementationKey);
    }

    @Override
    public void update(T datasetImplementation) {
        DatasetImplementationConfiguration.getInstance().update(datasetImplementation);
    }

    @Override
    public List<DatasetImplementation> getAll() {
        return DatasetImplementationConfiguration.getInstance()
                .getAll();
    }


}