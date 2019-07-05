package io.metadew.iesi.metadata.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.DataObjectConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataObjectOperation {

    private FrameworkExecution frameworkExecution;
    private String inputFile;
    private List<DataObject> dataObjects;
    private DataObject dataObject;
    private DataObjectConfiguration dataObjectConfiguration;
    private List<MetadataRepository> metadataRepositories;

    // Constructors
    public DataObjectOperation() {
    	
    }
    
    public DataObjectOperation(String inputFile) {
		this.setInputFile(inputFile);
		File file = new File(inputFile);
		if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
			this.parseFile();
		} else {
			this.parseYamlFile();
		}
    }

    public DataObjectOperation(FrameworkExecution frameworkExecution, String inputFile) {
        this.setFrameworkExecution(frameworkExecution);
        this.setInputFile(inputFile);
        File file = new File(inputFile);
        if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
            this.parseFile();
        } else {
            this.parseYamlFile();
        }
        this.setDataObjectConfiguration(
                new DataObjectConfiguration(this.getFrameworkExecution(), this.getDataObjects()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public DataObjectOperation(FrameworkExecution frameworkExecution,
                               MetadataRepository metadataRepositories, String inputFile) {
        this.setFrameworkExecution(frameworkExecution);
        this.setInputFile(inputFile);
        File file = new File(inputFile);
        if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
            this.parseFile();
        } else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
            this.parseYamlFile();
        }
        this.setMetadataRepositories(new ArrayList());
        this.getMetadataRepositories().add(metadataRepositories);
        this.setDataObjectConfiguration(new DataObjectConfiguration(this.getFrameworkExecution(),
                metadataRepositories, this.getDataObjects()));

    }

    public DataObjectOperation(FrameworkExecution frameworkExecution,
                               List<MetadataRepository> metadataRepositoryConfigurationList, String inputFile) {
        this.setFrameworkExecution(frameworkExecution);
        this.setMetadataRepositories(metadataRepositoryConfigurationList);
        this.setInputFile(inputFile);
        File file = new File(inputFile);
        if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
            this.parseFile();
        } else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
            this.parseYamlFile();
        }

    }

    // Methods
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void parseFile() {
        // Define input file
        File file = new File(this.getInputFile());
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            boolean jsonArray = true;

            while ((readLine = bufferedReader.readLine()) != null) {
                if (readLine.trim().toLowerCase().startsWith("[") && (!readLine.trim().equalsIgnoreCase(""))) {
                    jsonArray = true;
                    break;
                } else if (!readLine.trim().equalsIgnoreCase("")) {
                    jsonArray = false;
                    break;
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            if (jsonArray) {
                this.setDataObjects(objectMapper.readValue(file, new TypeReference<List<DataObject>>() {
                }));
            } else {
                this.setDataObject(objectMapper.readValue(file, new TypeReference<DataObject>() {
                }));
                this.setDataObjects(new ArrayList());
                this.getDataObjects().add(this.getDataObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void parseYamlFile() {
        // Define input file
        File file = new File(this.getInputFile());
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = "";
            boolean yamlArray = true;

            while ((readLine = bufferedReader.readLine()) != null) {
                if (readLine.trim().toLowerCase().startsWith("[") && (!readLine.trim().equalsIgnoreCase(""))) {
                    yamlArray = true;
                    break;
                } else if (!readLine.trim().equalsIgnoreCase("")) {
                    yamlArray = false;
                    break;
                }
            }

            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            if (yamlArray) {
                this.setDataObjects(objectMapper.readValue(file, new TypeReference<List<DataObject>>() {
                }));
            } else {
                this.setDataObject(objectMapper.readValue(file, new TypeReference<DataObject>() {
                }));
                this.setDataObjects(new ArrayList());
                this.getDataObjects().add(this.getDataObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO remove from this operation - create new one
    public void saveToMetadataRepository() {
        for (MetadataRepository metadataRepository : this.getMetadataRepositories()) {
            this.setDataObjectConfiguration(new DataObjectConfiguration(this.getFrameworkExecution(), metadataRepository, this.getDataObjects()));
            this.getDataObjectConfiguration().saveToMetadataRepository();
        }
    }

    // Getters and Setters
    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = FilenameUtils.normalize(inputFile);
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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

    public void setDataObjects(List<DataObject> dataObjects) {
        this.dataObjects = dataObjects;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public List<MetadataRepository> getMetadataRepositories() {
        return metadataRepositories;
    }

    public void setMetadataRepositories(
            List<MetadataRepository> metadataRepositories) {
        this.metadataRepositories = metadataRepositories;
    }

}