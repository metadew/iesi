package io.metadew.iesi.framework.execution;

import io.metadew.iesi.common.properties.PropertiesTools;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.configuration.framework.FrameworkConfiguration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

public class FrameworkRuntime {

	private String localHostChallengeFileName;

	private static FrameworkRuntime INSTANCE;

	public synchronized static FrameworkRuntime getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FrameworkRuntime();
		}
		return INSTANCE;
	}

	private FrameworkRuntime() {}

	public void init() {
		String runCacheFolderName = FrameworkConfiguration.getInstance().getMandatoryFrameworkFolder("run.cache").getAbsolutePath();
		FolderTools.createFolder(runCacheFolderName);

		String runSpoolFolderName = runCacheFolderName + File.separator + "spool";
		FolderTools.createFolder(runSpoolFolderName);

		String localHostChallenge = UUID.randomUUID().toString();
		this.localHostChallengeFileName = FilenameUtils.normalize(runCacheFolderName + File.separator + localHostChallenge + ".fwk");
		FileTools.appendToFile(localHostChallengeFileName, "", "localhost.challenge=" + localHostChallenge);

		// Initialize process id
		String processIdFileName = FilenameUtils.normalize(runCacheFolderName + File.separator + "processId.fwk");
		Properties processIdProperties = new Properties();
		processIdProperties.put("processId", "-1");
		PropertiesTools.setProperties(processIdFileName, processIdProperties);
	}


	public void terminate() {
		//FolderTools.deleteFolder(this.getRunCacheFolderName(), true);
	}

	public String getLocalHostChallengeFileName() {
		return localHostChallengeFileName;
	}

}

