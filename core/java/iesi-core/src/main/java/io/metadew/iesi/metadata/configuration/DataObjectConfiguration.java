package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepositorySaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public class DataObjectConfiguration {

    private List<DataObject> dataObjects;
    private static final Logger LOGGER = LogManager.getLogger();

    public DataObjectConfiguration(List<DataObject> dataObjects) {
        this.dataObjects = dataObjects;
    }
//
//    public DataObjectConfiguration() {
//        this(new ArrayList<>());
//    }
//
//    // TODO: bad!!
//    public DataObjectConfiguration(List<DataObject> dataObjects) {
//        this(dataObjects, null);
//    }


    // Methods
    public DataObject getDataObject(Object object) {
        String type = FrameworkObjectConfiguration.getFrameworkObjectType(object);
        return new DataObject(type, object);
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataObjectList;
    }

    public void saveToMetadataRepository(MetadataRepository metadataRepository) {
        for (DataObject dataObject : dataObjects) {
            try {
                metadataRepository.save(dataObject);
            } catch (MetadataRepositorySaveException e) {
                LOGGER.warn(MessageFormat.format("Failed to save {0} to repository", dataObject.getType()));
            }
        }
    }

    private void createFolder(String path, String folderName) {
        FolderTools.createFolder(path + File.separator + folderName);
    }

    public List<DataObject> getDataObjects() {
        return dataObjects;
    }

}