package io.metadew.iesi.datatypes.dataset;

import java.util.List;
import java.util.Optional;

public interface IDatasetService {

    boolean exists(DatasetKey datasetKey);

    boolean exists(String name);

    Optional<Dataset> get(DatasetKey datasetKey);

    List<Dataset> getAll();

    Optional<Dataset> getByName(String name);

    void create(Dataset dataset);

    List<Dataset> importDatasets(String textPlain);

    void delete(Dataset dataset);

    void delete(DatasetKey datasetKey);

    void update(Dataset dataset);

}
