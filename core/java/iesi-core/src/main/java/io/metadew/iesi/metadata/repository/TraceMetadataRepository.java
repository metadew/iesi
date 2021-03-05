package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class TraceMetadataRepository extends MetadataRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    public TraceMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "trace";
    }


    @SuppressWarnings("unused")
    @Override
    public void save(DataObject dataObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("trace")) {
            // TODO
        } else {
            LOGGER.trace(MessageFormat.format("Trace repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }
}
