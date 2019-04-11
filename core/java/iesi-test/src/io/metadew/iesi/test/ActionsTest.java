package io.metadew.iesi.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.test.launch.LaunchArgument;
import io.metadew.iesi.test.launch.Launcher;

public class ActionsTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {

		String repository = "c:/Data/metadew/iesi";
		String sandbox = "c:/Data/metadew/sandbox/iesi";
		String instance = "badger";
		String version = "alpha";

		String repositoryHome = repository;
		@SuppressWarnings("unused")
		String sandboxHome = sandbox;
		String instanceHome = sandbox + File.separator + instance;
		String versionHome = instanceHome + File.separator + version;

		String testConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
				+ File.separator + "conf" + File.separator + "actions";
		

		String testDataFolder = versionHome + File.separator + "data" + File.separator + "iesi-test";
		String actionsTestDataFolder = testDataFolder + File.separator + "actions";
		String actionsTestConfDataFolder = actionsTestDataFolder + File.separator + "conf";
		
		String metadataInNewFolder = versionHome + File.separator + "metadata" + File.separator + "in"+ File.separator + "new";
		
		FolderTools.createFolder(testDataFolder);
		FolderTools.createFolder(actionsTestDataFolder);
		FolderTools.createFolder(actionsTestConfDataFolder);
		
		FolderTools.copyFromFolderToFolder(testConfigurationHome, actionsTestConfDataFolder, false);
		FileTools.delete(actionsTestConfDataFolder + File.separator + ".gitkeep");
		
		
		// Load action tests
		final File[] confs = FolderTools
				.getFilesInFolder(actionsTestConfDataFolder, "regex", ".+\\.yml");
		
		List<LaunchArgument> inputArgs = new ArrayList();
		LaunchArgument ini = new LaunchArgument(true,"-ini","iesi-test.ini");
		inputArgs.add(ini);
		LaunchArgument exit = new LaunchArgument(true,"-exit","false");
		inputArgs.add(exit);
		LaunchArgument load = new LaunchArgument(false,"-load","");
		inputArgs.add(load);
		LaunchArgument type = new LaunchArgument(true,"-type","general");		
		inputArgs.add(type);

		for (final File conf : confs) {
			FileTools.copyFromFileToFile(conf.getAbsolutePath(), metadataInNewFolder + File.separator + conf.getName() );
			LaunchArgument files = new LaunchArgument(true,"-files",conf.getAbsolutePath());
			inputArgs.add(files);
			Launcher.execute("metadata",inputArgs);
			inputArgs.remove(files);
		}

		// Run action tests
		List<LaunchArgument> scriptInputArgs = new ArrayList();
		scriptInputArgs.add(ini);
		scriptInputArgs.add(exit);
		LaunchArgument env = new LaunchArgument(true,"-env","iesi-test");
		scriptInputArgs.add(env);
		LaunchArgument script = new LaunchArgument(true,"-script","fwk.dummy.1");
		scriptInputArgs.add(script);
		
		Launcher.execute("script",scriptInputArgs);

		scriptInputArgs.remove(script);
		
		script = new LaunchArgument(true,"-script","fwk.outputMessage.1");
		scriptInputArgs.add(script);

		Launcher.execute("script",scriptInputArgs);

	}
}