package io.metadew.iesi.framework.operation;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;

public class FrameworkPluginOperation {
	
	private FrameworkExecution frameworkExecution;
	private String pluginConfigurationFile;
	
	public FrameworkPluginOperation(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}
	
	public boolean verifyPlugins(String configurationToVerify) {
		boolean result = false;
		for (FrameworkPluginConfiguration frameworkPluginConfiguration : this.getFrameworkExecution().getFrameworkControl().getFrameworkPluginConfigurationList()) {
			StringBuilder configurationFile = new StringBuilder();
			configurationFile.append(frameworkPluginConfiguration.getFrameworkPlugin().getPath());
			configurationFile.append(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderPath("metadata.conf"));
			configurationFile.append(File.separator);
			configurationFile.append(configurationToVerify);
			String filePath = FilenameUtils.normalize(configurationFile.toString());
			if (FileTools.exists(filePath)) {
				this.setPluginConfigurationFile(filePath);
				result = true;
				break;
			}
		}
		return result;
	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public String getPluginConfigurationFile() {
		return pluginConfigurationFile;
	}

	public void setPluginConfigurationFile(String pluginConfigurationFile) {
		this.pluginConfigurationFile = pluginConfigurationFile;
	}
	
	
	
}