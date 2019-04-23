package io.metadew.iesi.metadata.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.DataObjectConfiguration;
import io.metadew.iesi.metadata.configuration.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;

public class DataObjectOperation {

	private FrameworkExecution frameworkExecution;
	private String inputFile;
	private List<DataObject> dataObjects;
	private DataObject dataObject;
	private DataObjectConfiguration dataObjectConfiguration;
	private List<MetadataRepositoryConfiguration> metadataRepositoryConfigurationList;

	// Constructors
	public DataObjectOperation() {

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataObjectOperation(FrameworkExecution frameworkExecution,
			MetadataRepositoryConfiguration metadataRepositoryConfiguration, String inputFile) {
		this.setFrameworkExecution(frameworkExecution);
		this.setInputFile(inputFile);
		File file = new File(inputFile);
		if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
			this.parseFile();
		} else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
			this.parseYamlFile();
		}
		this.setMetadataRepositoryConfigurationList(new ArrayList());
		this.getMetadataRepositoryConfigurationList().add(metadataRepositoryConfiguration);
		this.setDataObjectConfiguration(new DataObjectConfiguration(this.getFrameworkExecution(),
				metadataRepositoryConfiguration, this.getDataObjects()));

	}

	public DataObjectOperation(FrameworkExecution frameworkExecution,
			List<MetadataRepositoryConfiguration> metadataRepositoryConfigurationList, String inputFile) {
		this.setFrameworkExecution(frameworkExecution);
		this.setMetadataRepositoryConfigurationList(metadataRepositoryConfigurationList);
		this.setInputFile(inputFile);
		File file = new File(inputFile);
		if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
			this.parseFile();
		} else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
			this.parseYamlFile();
		}
		
	}

	// Methods
	@SuppressWarnings({ "unchecked", "rawtypes" })
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
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

	public void saveToMetadataRepository() {
		for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : this
				.getMetadataRepositoryConfigurationList()) {
			this.setDataObjectConfiguration(new DataObjectConfiguration(this.getFrameworkExecution(),
					metadataRepositoryConfiguration, this.getDataObjects()));
			this.getDataObjectConfiguration().saveToMetadataRepository();
		}
	}

	public String getMetadataRepositoryDdl() {
		StringBuilder output = new StringBuilder();
		for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : this
				.getMetadataRepositoryConfigurationList()) {
			this.setDataObjectConfiguration(new DataObjectConfiguration(this.getFrameworkExecution(),
					metadataRepositoryConfiguration, this.getDataObjects()));
			output.append(this.getDataObjectConfiguration().getMetadataRepositoryDdl());
		}
		return output.toString();
	}

	public void saveToMetadataFileStore() {
		for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : this
				.getMetadataRepositoryConfigurationList()) {
			this.setDataObjectConfiguration(new DataObjectConfiguration(this.getFrameworkExecution(),
					metadataRepositoryConfiguration, this.getDataObjects()));
			this.getDataObjectConfiguration().saveToMetadataFileStore();
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

	public List<MetadataRepositoryConfiguration> getMetadataRepositoryConfigurationList() {
		return metadataRepositoryConfigurationList;
	}

	public void setMetadataRepositoryConfigurationList(
			List<MetadataRepositoryConfiguration> metadataRepositoryConfigurationList) {
		this.metadataRepositoryConfigurationList = metadataRepositoryConfigurationList;
	}

}