package io.metadew.iesi.datatypes.dataset;

import java.util.List;
import java.util.Optional;

public interface IDatasetService {

    public Optional<Dataset> get(DatasetKey datasetKey);

    public List<Dataset> getAll();

    public Optional<Dataset> getByName(String name);

    public void create(Dataset dataset);

    public void delete(Dataset dataset);

    public void delete(DatasetKey datasetKey);

    public void update(Dataset dataset);

}
