package io.metadew.iesi.datatypes.dataset;

import java.util.List;
import java.util.Optional;

public interface IDatasetService {

    boolean exists(DatasetKey datasetKey);

    boolean exists(String name);

    boolean getIdByName(String name);

    Optional<Dataset> get(DatasetKey datasetKey);

    List<Dataset> getAll();

    Optional<Dataset> getByName(String name);

    void create(Dataset dataset);

    void delete(Dataset dataset);

    void delete(DatasetKey datasetKey);

    void update(Dataset dataset);

}
