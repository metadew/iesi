package io.metadew.iesi.framework.configuration;

import io.metadew.iesi.common.config.KeyValueConfigFile;
import io.metadew.iesi.connection.tools.FileTools;
import org.apache.logging.log4j.ThreadContext;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FrameworkConfiguration {

	private String frameworkCode="";
	private Path frameworkHome;

	private FrameworkGenerationRuleTypeConfiguration generationRuleTypeConfiguration;

	private static FrameworkConfiguration INSTANCE;

	public synchronized static FrameworkConfiguration getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FrameworkConfiguration();
		}
		return INSTANCE;
	}

	private FrameworkConfiguration() {}


	public void init() {
		this.frameworkCode = FrameworkSettings.IDENTIFIER.value();
		ThreadContext.put("fwk.code", frameworkCode);
		String configurationFile = FrameworkSettings.IDENTIFIER.value() + "-home.conf";
		if (System.getProperty(frameworkCode + ".home") != null) {
			this.frameworkHome = Paths.get(System.getProperty(frameworkCode  + ".home"));
		} else if (getClass().getResource(frameworkCode + ".home") != null) {
			this.frameworkHome = Paths.get(getClass().getResource(frameworkCode  + ".home").getFile());
		} else if (getClass().getResource(FrameworkSettings.IDENTIFIER.value() + "-home.conf") != null) {
			KeyValueConfigFile home = new KeyValueConfigFile(Paths.get(getClass().getResource(FrameworkSettings.IDENTIFIER.value() + "-home.conf").getFile()));
			this.frameworkHome = Paths.get(home.getProperties().getProperty(frameworkCode  + ".home"));
		} else if (FileTools.exists(configurationFile)) {
			KeyValueConfigFile home = new KeyValueConfigFile(Paths.get(".").resolve(configurationFile));
			this.frameworkHome = Paths.get(home.getProperties().getProperty(frameworkCode  + ".home"));
		} else {
			Path path = Paths.get(".").toAbsolutePath();
			throw new RuntimeException(frameworkCode  + ".home not found as System property or " + frameworkCode + "-home.conf not found at " + path.getRoot() + " or on classpath");
		}
		init(frameworkHome);
	}

	public void init(String frameworkHome) {
		init(Paths.get(frameworkHome));
	}

	public void init(Path frameworkHome) {
		this.frameworkCode = FrameworkSettings.IDENTIFIER.value();
		this.frameworkHome = frameworkHome;
		FrameworkFolderConfiguration folderConfiguration = FrameworkFolderConfiguration.getInstance();
		folderConfiguration.init(frameworkHome);

		FrameworkSettingConfiguration settingConfiguration = FrameworkSettingConfiguration.getInstance();
		settingConfiguration.init(frameworkHome);

		FrameworkActionTypeConfiguration actionTypeConfiguration = FrameworkActionTypeConfiguration.getInstance();
		actionTypeConfiguration.init(folderConfiguration);

		this.generationRuleTypeConfiguration = new FrameworkGenerationRuleTypeConfiguration(folderConfiguration);
	}

	public void initAssembly(String repositoryHome) {
		// TODO: add core substring in assembly context in order to start the framework with custom iesi home
		//  for testing purposes
		this.frameworkCode = FrameworkSettings.IDENTIFIER.value();
		ThreadContext.put("fwk.code", frameworkCode);
		this.frameworkHome = Paths.get(repositoryHome).resolve("core");

		FrameworkFolderConfiguration folderConfiguration = FrameworkFolderConfiguration.getInstance();
		folderConfiguration.init(frameworkHome);

		FrameworkSettingConfiguration settingConfiguration = FrameworkSettingConfiguration.getInstance();
		settingConfiguration.init(frameworkHome);
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


	public Path getFrameworkHome() {
		return frameworkHome;
	}

	public String getFrameworkCode() {
		return frameworkCode;
	}

	public FrameworkGenerationRuleTypeConfiguration getGenerationRuleTypeConfiguration() {
		return generationRuleTypeConfiguration;
	}

}