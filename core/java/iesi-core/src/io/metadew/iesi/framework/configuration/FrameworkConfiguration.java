package io.metadew.iesi.framework.configuration;

import io.metadew.iesi.common.config.KeyValueConfigFile;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class FrameworkConfiguration {

	private String frameworkCode;
	private String frameworkHome;
	private FrameworkFolderConfiguration folderConfiguration;
	private FrameworkSettingConfiguration settingConfiguration;
	private FrameworkActionTypeConfiguration actionTypeConfiguration;
	private FrameworkGenerationRuleTypeConfiguration generationRuleTypeConfiguration;

	public FrameworkConfiguration() {
		this.setFrameworkCode(FrameworkSettings.IDENTIFIER.value());
		this.initializeFrameworkHome();
		this.setFolderConfiguration(new FrameworkFolderConfiguration(this.getFrameworkHome()));
		this.setSettingConfiguration(new FrameworkSettingConfiguration(this.getFrameworkHome()));
		this.setActionTypeConfiguration(new FrameworkActionTypeConfiguration(this.getFolderConfiguration()));
		this.setGenerationRuleTypeConfiguration(
				new FrameworkGenerationRuleTypeConfiguration(this.getFolderConfiguration()));
	}

	public FrameworkConfiguration(String repositoryHome) {
		this.setFrameworkCode(FrameworkSettings.IDENTIFIER.value());
		// TODO: add core substring in assembly context in order to start the framework with custom iesi home
		//  for testing purposes
		this.setFrameworkHome(repositoryHome + File.separator + "core");
		this.setFolderConfiguration(new FrameworkFolderConfiguration(this.getFrameworkHome()));
		this.setSettingConfiguration(new FrameworkSettingConfiguration(this.getFrameworkHome()));
	}

	public FrameworkConfiguration(String frameworkHome, FrameworkFolderConfiguration frameworkFolderConfiguration, FrameworkSettingConfiguration frameworkSettingConfiguration,
								  FrameworkActionTypeConfiguration frameworkActionTypeConfiguration, FrameworkGenerationRuleTypeConfiguration frameworkGenerationRuleTypeConfiguration) {
		this.frameworkCode = FrameworkSettings.IDENTIFIER.value();
		this.frameworkHome = frameworkHome;
		this.folderConfiguration = frameworkFolderConfiguration;
		this.settingConfiguration = frameworkSettingConfiguration;
		this.actionTypeConfiguration = frameworkActionTypeConfiguration;
		this.generationRuleTypeConfiguration = frameworkGenerationRuleTypeConfiguration;
	}

	private void initializeFrameworkHome() {
		String configurationFile = FrameworkSettings.IDENTIFIER.value() + "-home.conf";
		Properties properties = new Properties();
		if (FileTools.exists(configurationFile)) {
			KeyValueConfigFile home = new KeyValueConfigFile(configurationFile);
			properties.putAll(home.getProperties());
		} else {
			throw new RuntimeException(configurationFile + " not found");
		}
		this.setFrameworkHome(properties.getProperty(this.getFrameworkCode() + ".home"));
	}

	public void setActionTypesFromPlugins(List<FrameworkPluginConfiguration> frameworkPluginConfigurationList) {
		this.getActionTypeConfiguration().setActionTypesFromPlugins(this.getFolderConfiguration(),
				frameworkPluginConfigurationList);
	}

	public void setGenerationRuleTypesFromPlugins(List<FrameworkPluginConfiguration> frameworkPluginConfigurationList) {
		this.getGenerationRuleTypeConfiguration().setGenerationRuleTypesFromPlugins(this.getFolderConfiguration(),
				frameworkPluginConfigurationList);
	}

	// Getters and Setters
	public FrameworkFolderConfiguration getFolderConfiguration() {
		return folderConfiguration;
	}

	public void setFolderConfiguration(FrameworkFolderConfiguration folderConfiguration) {
		this.folderConfiguration = folderConfiguration;
	}

	public FrameworkSettingConfiguration getSettingConfiguration() {
		return settingConfiguration;
	}

	public void setSettingConfiguration(FrameworkSettingConfiguration settingConfiguration) {
		this.settingConfiguration = settingConfiguration;
	}

	public String getFrameworkHome() {
		return frameworkHome;
	}

	public void setFrameworkHome(String frameworkHome) {
		this.frameworkHome = frameworkHome;
	}

	public String getFrameworkCode() {
		return frameworkCode;
	}

	public void setFrameworkCode(String frameworkCode) {
		this.frameworkCode = frameworkCode;
	}

	public FrameworkActionTypeConfiguration getActionTypeConfiguration() {
		return actionTypeConfiguration;
	}

	public void setActionTypeConfiguration(FrameworkActionTypeConfiguration actionTypeConfiguration) {
		this.actionTypeConfiguration = actionTypeConfiguration;
	}

	public FrameworkGenerationRuleTypeConfiguration getGenerationRuleTypeConfiguration() {
		return generationRuleTypeConfiguration;
	}

	public void setGenerationRuleTypeConfiguration(
			FrameworkGenerationRuleTypeConfiguration generationRuleTypeConfiguration) {
		this.generationRuleTypeConfiguration = generationRuleTypeConfiguration;
	}
}