package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepositorySaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DataObjectConfiguration {

    private List<DataObject> dataObjects;
    private MetadataRepository metadataRepository;
    private MetadataRepositoryOperation metadataRepositoryOperation;
    private static final Logger LOGGER = LogManager.getLogger();
    // Constructors

    public DataObjectConfiguration(List<DataObject> dataObjects, MetadataRepository metadataRepository, MetadataRepositoryOperation metadataRepositoryOperation) {
        this.dataObjects = dataObjects;
        this.metadataRepository = metadataRepository;
        this.metadataRepositoryOperation = metadataRepositoryOperation;
    }

    public DataObjectConfiguration() {
        this(new ArrayList<>());
    }

    // TODO: bad!!
    public DataObjectConfiguration(List<DataObject> dataObjects) {
        this(null, dataObjects);
    }

    public DataObjectConfiguration(MetadataRepository metadataRepository, List<DataObject> dataObjects) {
        this(dataObjects, metadataRepository, new MetadataRepositoryOperation(metadataRepository));
    }

    // Methods
    public DataObject getDataObject(Object object) {
        String type = FrameworkObjectConfiguration.getFrameworkObjectType(object);
        DataObject dataObject = new DataObject(type, object);
        return dataObject;
    }

    public String getDataObjectJSON(Object object) {
        DataObject dataObject = this.getDataObject(object);
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(dataObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getPrettyDataObjectJSON(Object object) {
        DataObject dataObject = this.getDataObject(object);
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean isJSONArray(String data) {
        if (data.trim().startsWith("[")) {
            return true;
        } else {
            return false;
        }
    }

    public DataObject getDataObject(String data) {
        DataObject dataObject = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            dataObject = objectMapper.readValue(data, new TypeReference<DataObject>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObject;
    }

    public List<DataObject> getDataArray(String data) {
        List<DataObject> dataObjectList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            dataObjectList = objectMapper.readValue(data, new TypeReference<List<DataObject>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObjectList;
    }

    public void saveToMetadataRepository() {
        for (DataObject dataObject : dataObjects) {
            try {
                this.getMetadataRepository().save(dataObject);
            } catch (MetadataRepositorySaveException e) {
                LOGGER.warn(MessageFormat.format("Failed to save {0} to repository", dataObject.getType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    private void createFolder(String path, String folderName) {
        FolderTools.createFolder(path + File.separator + folderName);
    }

    public List<DataObject> getDataObjects() {
        return dataObjects;
    }

    public void setDataObjects(List<DataObject> dataObjects) {
        this.dataObjects = dataObjects;
    }

    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public MetadataRepositoryOperation getMetadataRepositoryOperation() {
        return metadataRepositoryOperation;
    }

    public void setMetadataRepositoryOperation(MetadataRepositoryOperation metadataRepositoryOperation) {
        this.metadataRepositoryOperation = metadataRepositoryOperation;
    }

}