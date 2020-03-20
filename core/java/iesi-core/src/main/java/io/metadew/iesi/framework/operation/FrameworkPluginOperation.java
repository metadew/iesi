package io.metadew.iesi.framework.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.plugin.PluginConfiguration;
import io.metadew.iesi.framework.definition.FrameworkPlugin;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class FrameworkPluginOperation {

    private String pluginConfigurationFile;

    public FrameworkPluginOperation() {
    }

    public boolean verifyPlugins(String configurationToVerify) {
        boolean result = false;
        for (FrameworkPlugin frameworkPluginConfiguration : PluginConfiguration.getInstance().getFrameworkPluginMap().values()) {
            //configurationFile.append(frameworkPluginConfiguration.getFrameworkPlugin().getPath());
            String configurationFile = FrameworkConfiguration.getInstance().getMandatoryFrameworkFolder("metadata.conf").getPath() +
                    File.separator +
                    configurationToVerify;
            String filePath = FilenameUtils.normalize(configurationFile);
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

}