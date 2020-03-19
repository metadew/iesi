package io.metadew.iesi.framework.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.definition.FrameworkPlugin;
import io.metadew.iesi.framework.execution.FrameworkControl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
@Log4j2
public class FrameworkPluginOperation {

    private String pluginConfigurationFile;

    public FrameworkPluginOperation() {
    }

    public boolean verifyPlugins(String configurationToVerify) {
        boolean result = false;
        for (FrameworkPlugin frameworkPlugin : FrameworkControl.getInstance().getFrameworkPlugins()) {
            String configurationFile = frameworkPlugin.getPath() +
                    File.separator +
                    FrameworkFolderConfiguration.getInstance().getFolderPath("metadata.conf") +
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