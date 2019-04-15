package io.metadew.iesi.test.launch;

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
import io.metadew.iesi.metadata.configuration.DataObjectConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;

public class LaunchItemOperation {

	private String inputFile;
	private List<DataObject> dataObjects;
	private DataObject dataObject;
	private DataObjectConfiguration dataObjectConfiguration;

	// Constructors
	public LaunchItemOperation() {

	}

	public LaunchItemOperation(String inputFile) {
		this.setInputFile(inputFile);
		File file = new File(inputFile);
		if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
			this.parseFile();
		} else {
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
				if (readLine.trim().toLowerCase().startsWith("[") && (!readLine.trim().equals(""))) {
					jsonArray = true;
					break;
				} else if (!readLine.trim().equals("")) {
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
				if (readLine.trim().toLowerCase().startsWith("[") && (!readLine.trim().equals(""))) {
					yamlArray = true;
					break;
				} else if (!readLine.trim().equals("")) {
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

	// Getters and Setters
	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = FilenameUtils.normalize(inputFile);
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

}