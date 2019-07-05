package io.metadew.iesi.framework.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.GenerationRuleType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

public class FrameworkGenerationRuleTypeConfiguration {

	private HashMap<String, GenerationRuleType> generationRuleTypeMap;

	public FrameworkGenerationRuleTypeConfiguration(FrameworkFolderConfiguration frameworkFolderConfiguration) {
		this.initalizeValues(frameworkFolderConfiguration);
	}

	private void initalizeValues(FrameworkFolderConfiguration frameworkFolderConfiguration) {
		this.setGenerationRuleTypeMap(new HashMap<String, GenerationRuleType>());

		StringBuilder initFilePath = new StringBuilder();
		initFilePath.append(frameworkFolderConfiguration.getFolderAbsolutePath("metadata.conf"));
		initFilePath.append(File.separator);
		initFilePath.append("GenerationRuleTypes.json");

		DataObjectOperation dataObjectOperation = new DataObjectOperation();
		dataObjectOperation.setInputFile(initFilePath.toString());
		dataObjectOperation.parseFile();
		ObjectMapper objectMapper = new ObjectMapper();
		for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
			if (dataObject.getType().equalsIgnoreCase("generationruletype")) {
				GenerationRuleType generationRuleType = objectMapper.convertValue(dataObject.getData(), GenerationRuleType.class);
				this.getGenerationRuleTypeMap().put(generationRuleType.getName().toLowerCase(), generationRuleType);
			}
		}
	}

	public void setGenerationRuleTypesFromPlugins(FrameworkFolderConfiguration frameworkFolderConfiguration,
			List<FrameworkPluginConfiguration> frameworkPluginConfigurationList) {
		for (FrameworkPluginConfiguration frameworkPluginConfiguration : frameworkPluginConfigurationList) {
			StringBuilder initFilePath = new StringBuilder();
			initFilePath.append(frameworkPluginConfiguration.getFrameworkPlugin().getPath());
			initFilePath.append(frameworkFolderConfiguration.getFolderPath("metadata.conf"));
			initFilePath.append(File.separator);
			initFilePath.append("GenerationRuleTypes.json");
			String filePath = FilenameUtils.normalize(initFilePath.toString());

			if (FileTools.exists(filePath)) {
				DataObjectOperation dataObjectOperation = new DataObjectOperation();
				dataObjectOperation.setInputFile(filePath);
				dataObjectOperation.parseFile();
				ObjectMapper objectMapper = new ObjectMapper();
				for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
					if (dataObject.getType().equalsIgnoreCase("generationruletype")) {
						GenerationRuleType generationRuleType = objectMapper.convertValue(dataObject.getData(), GenerationRuleType.class);
						if (this.getGenerationRuleTypeMap().containsKey(generationRuleType.getName().toLowerCase())) {
							//System.out.println("item already present - skipping " + generationRuleType.getName());
							// TODO provide startup alert
						} else {
							this.getGenerationRuleTypeMap().put(generationRuleType.getName().toLowerCase(), generationRuleType);
						}
					}
				}
			}
		}
	}

	// Create Getters and Setters
	public GenerationRuleType getGenerationRuleType(String key) {
		return this.getGenerationRuleTypeMap().get(key.toLowerCase());
	}

	public String getGenerationRuleTypeClass(String key) {
		return this.getGenerationRuleTypeMap().get(key.toLowerCase()).getClassName();
	}
	
	public HashMap<String, GenerationRuleType> getGenerationRuleTypeMap() {
		return generationRuleTypeMap;
	}

	public void setGenerationRuleTypeMap(HashMap<String, GenerationRuleType> generationRuleTypeMap) {
		this.generationRuleTypeMap = generationRuleTypeMap;
	}

}