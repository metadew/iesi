package io.metadew.iesi.framework.execution;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import io.metadew.iesi.common.properties.PropertiesTools;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkKeywords;
import io.metadew.iesi.framework.control.ProcessIdentifierController;

public class FrameworkRuntime {

	private FrameworkConfiguration frameworkConfiguration;
	private String runCacheFolderName;
	private String localHostChallenge;
	private String localHostChallengeFileName;
	private String runSpoolFolderName;
	private String processIdFileName;
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

		// Create spool folder
		this.setRunSpoolFolderName(
				this.getRunCacheFolderName() + File.separator + "spool");
		FolderTools.createFolder(this.getRunSpoolFolderName());

		// Create localhost challenge
		this.setLocalHostChallenge(UUID.randomUUID().toString());
		this.setLocalHostChallengeFileName(FilenameUtils.normalize(this.getRunCacheFolderName() + File.separator + this.getLocalHostChallenge()  + ".fwk"));
		FileTools.appendToFile(this.getLocalHostChallengeFileName(), "", "localhost.challenge=" + this.getLocalHostChallenge());

		// Initialize process id
		this.setProcessIdFileName(FilenameUtils.normalize(this.getRunCacheFolderName() + File.separator  + "processId.fwk"));
		Properties processIdProperties = new Properties();
		processIdProperties.put("processId", "-1");
		PropertiesTools.setProperties(this.getProcessIdFileName(), processIdProperties);
	}
	
	public Long getNextProcessId() {
		String spoolFileName = this.getRunSpoolFolderName() + File.separator + UUID.randomUUID().toString() + ".fwk";
		ProcessIdentifierController.getNextProcessId(this.getProcessIdFileName(), spoolFileName);
		Long processId = Long.parseLong(PropertiesTools.getProperty(spoolFileName, FrameworkKeywords.PROCESSID.value()));
		FileTools.delete(spoolFileName);
		return processId;
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

	public String getProcessIdFileName() {
		return processIdFileName;
	}

	public void setProcessIdFileName(String processIdFileName) {
		this.processIdFileName = processIdFileName;
	}

	public String getRunSpoolFolderName() {
		return runSpoolFolderName;
	}

	public void setRunSpoolFolderName(String runSpoolFolderName) {
		this.runSpoolFolderName = runSpoolFolderName;
	}

}

