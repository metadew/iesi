package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Log4j2
public class DataMetadataRepository extends MetadataRepository {

    public DataMetadataRepository(String instance, RepositoryCoordinator repositoryCoordinator) {
        super(instance, repositoryCoordinator);
        DatasetConfiguration.getInstance().init(this);
    }

    @Override
    public String getCategory() {
        return "data";
    }

    @Override
    public void save(DataObject dataObject) throws MetadataRepositorySaveException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("dataset")) {
            InMemoryDatasetImplementation inMemoryDatasetImplementation = (InMemoryDatasetImplementation) objectMapper.convertValue(dataObject, Metadata.class);
            save(inMemoryDatasetImplementation);
        } else {
            log.trace(MessageFormat.format("Design repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(InMemoryDatasetImplementation inMemoryDatasetImplementation) {
        log.info(MessageFormat.format("Saving dataset {0} into design repository", inMemoryDatasetImplementation.toString()));
        try {
            DatasetImplementationConfiguration.getInstance().insert(inMemoryDatasetImplementation);
        } catch (MetadataAlreadyExistsException e) {
            log.info(MessageFormat.format("dataset {0}-{1} already exists in design repository. Updating to new definition", inMemoryDatasetImplementation.getName()));
            DatasetImplementationConfiguration.getInstance().update(inMemoryDatasetImplementation);
        }
    }

}
