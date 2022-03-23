package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
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
    public Optional<Dataset> get(DatasetKey datasetKey) {
        if (!exists(datasetKey)) {
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
    public List<Dataset> importDatasets(String textPlain) {
        ObjectMapper objectMapper = new ObjectMapper();
        DataObjectOperation dataObjectOperation = new DataObjectOperation(textPlain);

        return dataObjectOperation.getDataObjects().stream().map((dataObject) -> {
            Dataset dataset = (Dataset) objectMapper.convertValue(dataObject, Metadata.class);
            if (DatasetConfiguration.getInstance().exists(dataset.getMetadataKey())) {
                log.info(MessageFormat.format("dataset {0} already exists in data repository. Updating to new definition", dataset.getName()));
                this.update(dataset);
            }  else {
                this.create(dataset);
            }
            return dataset;
        }).collect(Collectors.toList());
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
