package io.metadew.iesi.framework.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;
import io.metadew.iesi.metadata.repository.configuration.MetadataRepositoryConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.common.config.KeyValueConfigFile;
import io.metadew.iesi.common.config.KeyValueConfigList;
import io.metadew.iesi.common.config.LinuxConfigFile;
import io.metadew.iesi.common.config.WindowsConfigFile;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;

public class FrameworkControl {

	private Properties properties;
	private List<MetadataRepositoryConfiguration> metadataRepositoryConfigurations;
	private List<FrameworkPluginConfiguration> frameworkPluginConfigurationList;
	private String logonType;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FrameworkControl(FrameworkConfiguration frameworkConfiguration, String logonType, FrameworkInitializationFile frameworkInitializationFile, FrameworkCrypto frameworkCrypto) {
		try {
			this.setLogonType(logonType);
			this.setProperties(new Properties());
			this.setMetadataRepositoryConfigurations(new ArrayList());
			this.setFrameworkPluginConfigurationList(new ArrayList());
			this.getProperties().put(frameworkConfiguration.getFrameworkCode() + ".home",
					frameworkConfiguration.getFrameworkHome());
			this.readSettingFiles(frameworkConfiguration, frameworkInitializationFile.getName(), frameworkCrypto);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Issue setting config tools " + e);
		}

	}

	public FrameworkControl(FrameworkConfiguration frameworkConfiguration, String assemblyHome, String repositoryHome) {
		try {
			this.setProperties(new Properties());
			this.addSetting("iesi.home", assemblyHome);
			this.addSetting("iesi.identifier", "iesi");
			this.addSetting("iesi.metadata.repository.instance.name", "");
		} catch (Exception e) {
			throw new RuntimeException("Issue setting config tools " + e);
		}

	}

	// Methods
	private void readSettingFiles(FrameworkConfiguration frameworkConfiguration, String initializationFile, FrameworkCrypto frameworkCrypto) {
		try {
			File file = new File(this.resolveConfiguration("#" + frameworkConfiguration.getFrameworkCode()
					+ ".home#/conf/" + initializationFile));
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String readLine = "";
			
			while ((readLine = bufferedReader.readLine()) != null) {
				String innerpart = readLine.trim();
				String[] parts = innerpart.split(",");
				
				String key = parts[0];
				String type = parts[1];
				String value = parts[2];
				value = this.resolveConfiguration(value);
				
				ObjectMapper objectMapper = new ObjectMapper();
				ConfigFile configFile = null;
				if (key.equalsIgnoreCase("linux")) {
					LinuxConfigFile linuxConfigFile = new LinuxConfigFile(this, value);
					configFile = objectMapper.convertValue(linuxConfigFile, ConfigFile.class);
				} else if (key.equalsIgnoreCase("windows")) {
					WindowsConfigFile windowsConfigFile = new WindowsConfigFile(this, value);
					configFile = objectMapper.convertValue(windowsConfigFile, ConfigFile.class);
				} else if (key.equalsIgnoreCase("keyvalue")) {
					KeyValueConfigFile keyValueConfigFile = new KeyValueConfigFile(this, value);
					configFile = objectMapper.convertValue(keyValueConfigFile, ConfigFile.class);
				}

				if (type.trim().equalsIgnoreCase("repository")) {
					MetadataRepositoryConfiguration metadataRepositoryConfiguration = new MetadataRepositoryConfiguration(configFile, frameworkConfiguration.getSettingConfiguration(), frameworkCrypto);
					this.getMetadataRepositoryConfigurations().add(metadataRepositoryConfiguration);
				} else if (type.trim().equalsIgnoreCase("plugin")) {
					FrameworkPluginConfiguration frameworkPluginConfiguration = new FrameworkPluginConfiguration(frameworkConfiguration, configFile);
					this.getFrameworkPluginConfigurationList().add(frameworkPluginConfiguration);
				} else {
					this.getProperties().putAll(configFile.getProperties());
				}

			}

		} catch (Exception e) {
			throw new RuntimeException("Invalid setting file layout " + e);
		}
	}

	public void setSettingsList(String input) {
		KeyValueConfigList keyValueConfigList = new KeyValueConfigList(this, input);
		this.getProperties().putAll(keyValueConfigList.getProperties());
	}

	public String resolveConfiguration(String input) {
		int openPos;
		int closePos;
		String variable_char = "#";
		String midBit;
		String replaceValue = null;
		String temp = input;
		while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
			openPos = temp.indexOf(variable_char);
			closePos = temp.indexOf(variable_char, openPos + 1);
			midBit = temp.substring(openPos + 1, closePos);

			// Try to find a configuration value
			// If none is found, null is set by default
			try {
				replaceValue = this.getProperty(midBit);
			} catch (Exception e) {
				replaceValue = null;
			}

			// Replacing the value if found
			if (replaceValue != null) {
				input = input.replaceAll(variable_char + midBit + variable_char, replaceValue);
			}
			temp = temp.substring(closePos + 1, temp.length());

		}
		return input;
	}

	public void addKeyValueConfigFile(String path) {
		try {
			KeyValueConfigFile keyValueConfigFile = new KeyValueConfigFile(this, path);
			this.getProperties().putAll(keyValueConfigFile.getProperties());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSetting(String key, String value) {
		this.getProperties().put(key, value);
	}

	public String getProperty(String input) {
		String output = this.getProperties().getProperty(input);
		if (output == null) {
			throw new RuntimeException("Unknown configuration value lookup requested: " + input);
		}
		return output;
	}

	public ConfigFile getConfigFile(String type, String filePath) {
		ObjectMapper objectMapper = new ObjectMapper();
		ConfigFile configFile = null;
		if (type.equalsIgnoreCase("linux")) {
			LinuxConfigFile linuxConfigFile = new LinuxConfigFile(this, filePath);
			configFile = objectMapper.convertValue(linuxConfigFile, ConfigFile.class);
		} else if (type.equalsIgnoreCase("windows")) {
			WindowsConfigFile windowsConfigFile = new WindowsConfigFile(this, filePath);
			configFile = objectMapper.convertValue(windowsConfigFile, ConfigFile.class);
		} else if (type.equalsIgnoreCase("keyvalue")) {
			KeyValueConfigFile keyValueConfigFile = new KeyValueConfigFile(this, filePath);
			configFile = objectMapper.convertValue(keyValueConfigFile, ConfigFile.class);
		}

		configFile.setFilePath(filePath);

		return configFile;
	}

	// Getters and Setters
	public Properties getProperties() {
		return properties;
	}

	private void setProperties(Properties properties) {
		this.properties = properties;
	}

	public List<MetadataRepositoryConfiguration> getMetadataRepositoryConfigurations() {
		return metadataRepositoryConfigurations;
	}

	public void setMetadataRepositoryConfigurations(
			List<MetadataRepositoryConfiguration> metadataRepositoryConfigurations) {
		this.metadataRepositoryConfigurations = metadataRepositoryConfigurations;
	}

	public String getLogonType() {
		return logonType;
	}

	public void setLogonType(String logonType) {
		this.logonType = logonType;
	}


	public List<FrameworkPluginConfiguration> getFrameworkPluginConfigurationList() {
		return frameworkPluginConfigurationList;
	}

	public void setFrameworkPluginConfigurationList(List<FrameworkPluginConfiguration> frameworkPluginConfigurationList) {
		this.frameworkPluginConfigurationList = frameworkPluginConfigurationList;
	}

}