package io.metadew.iesi.metadata.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.metadata.configuration.DataObjectConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class DataObjectOperation {
    private Path inputFile;
    private String inputString;
    private List<DataObject> dataObjects;
    private DataObject dataObject;
    private DataObjectConfiguration dataObjectConfiguration;

    public DataObjectOperation(Path inputFile) {
        this.inputFile = inputFile;
        parse();
    }

    public DataObjectOperation(String inputString) {
        this.inputString = inputString;
        parse();
    }

    public void parse() {
        try {
            if (inputString == null && inputFile != null) {
                inputString = new String(Files.readAllBytes(inputFile));
                if (FileTools.getFileExtension(inputFile.toFile()).equals("json")) {
                    this.parseJSON();
                    return;
                }
                this.parseYAML();
                return;
            } else if (inputString != null && inputFile == null) {
                if (inputString.trim().startsWith("{") || inputString.trim().startsWith("[")) {
                    this.parseJSON();
                    return;
                }
                this.parseYAML();
                return;
            }
            log.warn("No valid format found for the provided metadata");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (JSONException e) {
            this.parseYAML();
        }
    }


    // Methods
    public void parseJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        boolean jsonArray = true;
        try {
            new JSONArray(inputString);
        } catch (JSONException e) {
            jsonArray = false;
        }

        try {
            if (jsonArray) {
                dataObjects = objectMapper.readValue(inputString, new TypeReference<List<DataObject>>() {});
            } else {
                dataObject = objectMapper.readValue(inputString, new TypeReference<DataObject>() {});
                dataObjects = Stream.of(dataObject).collect(Collectors.toList());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void parseYAML() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        BufferedReader bufferedReader = new BufferedReader(new StringReader(inputString));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().toLowerCase().startsWith("---")) {
                    // TODO: add support for multiple documents in file
                    continue;
                } else if (line.trim().toLowerCase().startsWith("-")) {
                    dataObjects = objectMapper.readValue(inputString, new TypeReference<List<DataObject>>() {});
                    break;
                } else {
                    dataObject = objectMapper.readValue(inputString, new TypeReference<DataObject>() {});
                    dataObjects = Stream.of(dataObject).collect(Collectors.toList());
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    // TODO remove from this operation - create new one
    public void saveToMetadataRepository(List<MetadataRepository> metadataRepositories) {
        for (MetadataRepository metadataRepository : metadataRepositories) {
            this.setDataObjectConfiguration(new DataObjectConfiguration(dataObjects));
            this.getDataObjectConfiguration().saveToMetadataRepository(metadataRepository);
        }
    }

    public void setInputFile(String inputFile) {
        setInputFile(Paths.get(inputFile));
    }

    public void setInputFile(Path inputFile) {
        this.inputFile = inputFile;
    }

    public DataObjectConfiguration getDataObjectConfiguration() {
        return dataObjectConfiguration;
    }

    public void setDataObjectConfiguration(DataObjectConfiguration dataObjectConfiguration) {
        this.dataObjectConfiguration = dataObjectConfiguration;
    }

    public List<DataObject> getDataObjects() {
        return dataObjects;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

}