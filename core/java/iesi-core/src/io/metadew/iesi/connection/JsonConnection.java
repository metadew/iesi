package io.metadew.iesi.connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.DataObject;

import java.util.List;

/**
 * Connection object for JSON files.
 *
 * @author peter.billen
 */
public class JsonConnection {


    // Constructors
    public JsonConnection() {

    }

    public DataObject mapDataObject(String data) {
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

    public List<DataObject> mapDataArray(String data) {
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

}