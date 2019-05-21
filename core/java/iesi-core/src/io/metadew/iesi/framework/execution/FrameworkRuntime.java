package io.metadew.iesi.framework.execution;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.UUID;

public class FrameworkRuntime {

    private FrameworkConfiguration frameworkConfiguration;
    private String runCacheFolderName;
    private String localHostChallenge;
    private String localHostChallengeFileName;
    private String runId;

    public FrameworkRuntime(FrameworkConfiguration frameworkConfiguration) {
        this.setFrameworkConfiguration(frameworkConfiguration);

        // Create run id
        this.setRunId(UUID.randomUUID().toString());

        // Create run cache folder
        this.setRunCacheFolderName(
                this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.cache")
                        + File.separator + this.getRunId());
        FolderTools.createFolder(this.getRunCacheFolderName());

        // Create localhost challenge
        this.setLocalHostChallenge(UUID.randomUUID().toString());
        this.setLocalHostChallengeFileName(FilenameUtils.normalize(this.getRunCacheFolderName() + File.separator + this.getLocalHostChallenge() + ".fwk"));
        FileTools.appendToFile(this.getLocalHostChallengeFileName(), "", "localhost.challenge=" + this.getLocalHostChallenge());
    }

    public void terminate() {
        //FolderTools.deleteFolder(this.getRunCacheFolderName(), true);
    }

    // Getters and setters
    public String getRunCacheFolderName() {
        return runCacheFolderName;
    }

    public void setRunCacheFolderName(String runCacheFolderName) {
        this.runCacheFolderName = runCacheFolderName;
    }

    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getLocalHostChallengeFileName() {
        return localHostChallengeFileName;
    }

    public void setLocalHostChallengeFileName(String localHostChallengeFileName) {
        this.localHostChallengeFileName = localHostChallengeFileName;
    }

    public String getLocalHostChallenge() {
        return localHostChallenge;
    }

    public void setLocalHostChallenge(String localHostChallenge) {
        this.localHostChallenge = localHostChallenge;
    }

}