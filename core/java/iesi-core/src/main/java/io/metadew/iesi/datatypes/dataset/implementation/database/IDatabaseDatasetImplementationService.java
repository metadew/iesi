package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;

import java.util.List;
import java.util.Optional;

public interface IDatabaseDatasetImplementationService extends IDatasetImplementationService<DatabaseDatasetImplementation> {

    boolean exists(DatasetImplementationKey datasetImplementationKey);

    boolean exists(String name, List<String> labels);

    Optional<DatasetImplementation> get(DatasetImplementationKey datasetImplementationKey);

    void create(DatabaseDatasetImplementation datasetImplementation);

    void delete(DatabaseDatasetImplementation datasetImplementation);

    void delete(DatasetImplementationKey datasetImplementationKey);

    void deleteByDatasetId(DatasetKey datasetKey);

    void update(DatabaseDatasetImplementation datasetImplementation);

    List<DatasetImplementation> getAll();

    List<DatasetImplementation> getByDatasetId(DatasetKey datasetKey);

    Optional<DatabaseDatasetImplementation> getDatasetImplementation(String name, List<String> labels);

    Optional<DatabaseDatasetImplementation> getDatasetImplementation(DatasetKey datasetKey, List<String> labels);

}