package io.metadew.iesi.framework.configuration;

import io.metadew.iesi.common.config.KeyValueConfigFile;
import io.metadew.iesi.connection.tools.FileTools;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FrameworkConfiguration {

	private String frameworkCode;
	private String frameworkHome;

	private FrameworkGenerationRuleTypeConfiguration generationRuleTypeConfiguration;

	private static FrameworkConfiguration INSTANCE;

	public synchronized static FrameworkConfiguration getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FrameworkConfiguration();
		}
		return INSTANCE;
	}

	private FrameworkConfiguration() {}


//	public FrameworkConfiguration() {
//		this.setFrameworkCode(FrameworkSettings.IDENTIFIER.value());
//		this.initializeFrameworkHome();
//		this.setFolderConfiguration(new FrameworkFolderConfiguration(this.getFrameworkHome()));
//		this.setSettingConfiguration(new FrameworkSettingConfiguration(this.getFrameworkHome()));
//		this.setActionTypeConfiguration(new FrameworkActionTypeConfiguration(this.getFolderConfiguration()));
//		this.setGenerationRuleTypeConfiguration(
//				new FrameworkGenerationRuleTypeConfiguration(this.getFolderConfiguration()));
//	}

	public void init() {
		this.frameworkCode = FrameworkSettings.IDENTIFIER.value();
		ThreadContext.put("fwk.code", frameworkCode);
		String configurationFile = FrameworkSettings.IDENTIFIER.value() + "-home.conf";
		if (System.getProperty(frameworkCode + ".home") != null) {
			this.frameworkHome = System.getProperty(frameworkCode  + ".home");
		} else if (getClass().getResource(FrameworkSettings.IDENTIFIER.value() + "-home.conf") != null) {
			KeyValueConfigFile home = new KeyValueConfigFile(getClass().getResource(FrameworkSettings.IDENTIFIER.value() + "-home.conf").getFile());
			this.frameworkHome = home.getProperties().getProperty(frameworkCode  + ".home");
		} else if (FileTools.exists(configurationFile)) {
			KeyValueConfigFile home = new KeyValueConfigFile(configurationFile);
			this.frameworkHome = home.getProperties().getProperty(frameworkCode  + ".home");
		} else {
			Path path = Paths.get(".").toAbsolutePath();
			throw new RuntimeException(frameworkCode  + ".home not found as System property or " + frameworkCode + "-home.conf not found at " + path.getRoot() + " or on classpath");
		}

		FrameworkFolderConfiguration folderConfiguration = FrameworkFolderConfiguration.getInstance();
		folderConfiguration.init(frameworkHome);

		FrameworkSettingConfiguration settingConfiguration = FrameworkSettingConfiguration.getInstance();
		settingConfiguration.init(frameworkHome);

		FrameworkActionTypeConfiguration actionTypeConfiguration = FrameworkActionTypeConfiguration.getInstance();
		actionTypeConfiguration.init(folderConfiguration);

		this.generationRuleTypeConfiguration = new FrameworkGenerationRuleTypeConfiguration(folderConfiguration);
	}

	public void init(String repositoryHome) {
		// TODO: add core substring in assembly context in order to start the framework with custom iesi home
		//  for testing purposes
		this.frameworkCode = FrameworkSettings.IDENTIFIER.value();
		ThreadContext.put("fwk.code", frameworkCode);
		this.frameworkHome = repositoryHome + File.separator + "core";

		FrameworkFolderConfiguration folderConfiguration = FrameworkFolderConfiguration.getInstance();
		folderConfiguration.init(frameworkHome);

		FrameworkSettingConfiguration settingConfiguration = FrameworkSettingConfiguration.getInstance();
		settingConfiguration.init(frameworkHome);
	}

	public void init(String frameworkHome, FrameworkGenerationRuleTypeConfiguration frameworkGenerationRuleTypeConfiguration) {
		this.frameworkCode = FrameworkSettings.IDENTIFIER.value();
		ThreadContext.put("fwk.code", frameworkCode);
		this.frameworkHome = frameworkHome;
		FrameworkFolderConfiguration folderConfiguration = FrameworkFolderConfiguration.getInstance();
		folderConfiguration.init(frameworkHome);

		FrameworkSettingConfiguration settingConfiguration = FrameworkSettingConfiguration.getInstance();
		settingConfiguration.init(frameworkHome);


		FrameworkActionTypeConfiguration actionTypeConfiguration = FrameworkActionTypeConfiguration.getInstance();
		actionTypeConfiguration.init(folderConfiguration);

		this.generationRuleTypeConfiguration = frameworkGenerationRuleTypeConfiguration;
	}

//	private void initializeFrameworkHome() {
//		String configurationFile = FrameworkSettings.IDENTIFIER.value() + "-home.conf";
//		Properties properties = new Properties();
//		if (FileTools.exists(configurationFile)) {
//			KeyValueConfigFile home = new KeyValueConfigFile(configurationFile);
//			properties.putAll(home.getProperties());
//		} else {
//			Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
//			System.out.println("Working dir" + path.toString());
//			throw new RuntimeException(configurationFile + " not found at " + path.getRoot());
//		}
//		this.setFrameworkHome(properties.getProperty(this.getFrameworkCode() + ".home"));
//	}


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

	public FrameworkGenerationRuleTypeConfiguration getGenerationRuleTypeConfiguration() {
		return generationRuleTypeConfiguration;
	}

	public void setGenerationRuleTypeConfiguration(
			FrameworkGenerationRuleTypeConfiguration generationRuleTypeConfiguration) {
		this.generationRuleTypeConfiguration = generationRuleTypeConfiguration;
	}
}