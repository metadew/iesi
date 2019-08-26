package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.feature.FeatureConfiguration;
import io.metadew.iesi.metadata.configuration.exception.FeatureAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.FeatureDoesNotExistException;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.feature.Feature;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class CatalogMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public CatalogMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "CatalogTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
    	return "CatalogObjects.json";
    }

    @Override
    public String getCategory() {
    	return "catalog";
    }

    @Override
    public String getCategoryPrefix() {
        return "CAT";
    }

    @Override
    public void save(DataObject dataObject) throws MetadataRepositorySaveException {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("feature")) {
            Feature feature = objectMapper.convertValue(dataObject.getData(), Feature.class);
            save(feature);
        } else {
            LOGGER.trace(MessageFormat.format("Catalog repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(Feature feature) throws MetadataRepositorySaveException {
        FeatureConfiguration featureConfiguration = new FeatureConfiguration();
        try {
            featureConfiguration.insertFeature(feature);
        } catch (FeatureAlreadyExistsException e) {
            try {
                featureConfiguration.updateFeature(feature);
            } catch (FeatureDoesNotExistException ex) {
                throw new MetadataRepositorySaveException(ex);
            }
        }
    }

}
