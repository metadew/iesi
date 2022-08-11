package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DatasetService implements IDatasetService {

    private final DatasetConfiguration datasetConfiguration;

    public DatasetService(DatasetConfiguration datasetConfiguration) {
        this.datasetConfiguration = datasetConfiguration;
    }


    @Override
    public boolean exists(DatasetKey datasetKey) {
        return datasetConfiguration.exists(datasetKey);
    }

    @Override
    public boolean exists(String name) {
        return datasetConfiguration.existsByName(name);
    }

    @Override
    public Optional<Dataset> get(DatasetKey datasetKey) {
        if (!exists(datasetKey)) {
            throw new MetadataDoesNotExistException(datasetKey);
        }
        return datasetConfiguration.get(datasetKey);
    }

    @Override
    public List<Dataset> getAll() {
        return datasetConfiguration.getAll();
    }

    @Override
    public Optional<Dataset> getByName(String name) {
        return datasetConfiguration.getByName(name);
    }

    @Override
    public void create(Dataset dataset) {
        datasetConfiguration.insert(dataset);
    }

    @Override
    public List<Dataset> importDatasets(String textPlain) {
        ObjectMapper objectMapper = new ObjectMapper();
        DataObjectOperation dataObjectOperation = new DataObjectOperation(textPlain);

        return dataObjectOperation.getDataObjects().stream().map((dataObject) -> {
            Dataset dataset = (Dataset) objectMapper.convertValue(dataObject, Metadata.class);
            if (datasetConfiguration.exists(dataset.getMetadataKey())) {
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
        datasetConfiguration.delete(datasetKey);
    }

    @Override
    public void update(Dataset dataset) {
        datasetConfiguration.update(dataset);
    }
}
