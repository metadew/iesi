package io.metadew.iesi.common;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Lazy
public class FrameworkRuntime {

	private Path localHostChallengePath;

	private final FrameworkConfiguration frameworkConfiguration;

	public FrameworkRuntime(FrameworkConfiguration frameworkConfiguration) {
		this.frameworkConfiguration = frameworkConfiguration;
	}

	@PostConstruct
	private void postConstruct() throws IOException {
		Path runCacheFolderName = frameworkConfiguration
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
	}

	public Path getLocalHostChallengeFileName() {
		return localHostChallengePath;
	}

}

