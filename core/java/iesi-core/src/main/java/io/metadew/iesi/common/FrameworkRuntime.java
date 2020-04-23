package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FrameworkRuntime {

	private static FrameworkRuntime INSTANCE;
	private Path localHostChallengePath;
	public synchronized static FrameworkRuntime getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FrameworkRuntime();
		}
		return INSTANCE;
	}

	private FrameworkRuntime() {}

	public void init() throws IOException {
		Path runCacheFolderName = FrameworkConfiguration.getInstance()
				.getMandatoryFrameworkFolder("run.cache")
				.getAbsolutePath();
		Files.createDirectories(runCacheFolderName);
		Files.createDirectories(runCacheFolderName.resolve("spool"));
		String localHostChallenge = UUID.randomUUID().toString();
		localHostChallengePath = runCacheFolderName.resolve(localHostChallenge + ".fwk");
		Files.createFile(localHostChallengePath);
		Files.write(runCacheFolderName.resolve(localHostChallenge + ".fwk"), ("localhost.challenge=" + localHostChallenge).getBytes(StandardCharsets.UTF_8));
	}


	public void terminate() {
		//FolderTools.deleteFolder(this.getRunCacheFolderName(), true);
	}

	public Path getLocalHostChallengeFileName() {
		return localHostChallengePath;
	}

}

