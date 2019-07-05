package io.metadew.iesi.framework.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class FrameworkPluginOperation {

    private FrameworkInstance frameworkInstance;
    private String pluginConfigurationFile;

    public FrameworkPluginOperation(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public boolean verifyPlugins(String configurationToVerify) {
        boolean result = false;
        for (FrameworkPluginConfiguration frameworkPluginConfiguration : this.getFrameworkInstance().getFrameworkControl().getFrameworkPluginConfigurationList()) {
            StringBuilder configurationFile = new StringBuilder();
            configurationFile.append(frameworkPluginConfiguration.getFrameworkPlugin().getPath());
            configurationFile.append(this.getFrameworkInstance().getFrameworkConfiguration().getFolderConfiguration().getFolderPath("metadata.conf"));
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
    public String getPluginConfigurationFile() {
        return pluginConfigurationFile;
    }

    public void setPluginConfigurationFile(String pluginConfigurationFile) {
        this.pluginConfigurationFile = pluginConfigurationFile;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}