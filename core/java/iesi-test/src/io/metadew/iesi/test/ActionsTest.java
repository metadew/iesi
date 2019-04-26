package io.metadew.iesi.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.test.launch.LaunchArgument;
import io.metadew.iesi.test.launch.LaunchItem;
import io.metadew.iesi.test.launch.LaunchItemOperation;
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

		String testDataHome = repositoryHome + File.separator + "test" + File.separator + "data";
		String testFwkConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
				+ File.separator + "conf" + File.separator + "fwk";
		String testLaunchConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
				+ File.separator + "conf" + File.separator + "launch";
		String testDefConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
				+ File.separator + "conf" + File.separator + "def";
		String testConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
				+ File.separator + "conf" + File.separator + "actions";
		

		String versionHomeConfFolder = versionHome + File.separator + "conf";
		String testDataFolder = versionHome + File.separator + "data" + File.separator + "iesi-test";
		String actionsTestDataFolder = testDataFolder + File.separator + "actions";
		String actionsTestConfDataFolder = actionsTestDataFolder + File.separator + "conf";
		String actionsTestDefDataFolder = actionsTestDataFolder + File.separator + "def";
		String actionsTestDataDataFolder = actionsTestDataFolder + File.separator + "data";
		
		String metadataInNewFolder = versionHome + File.separator + "metadata" + File.separator + "in"+ File.separator + "new";
		
		FolderTools.createFolder(testDataFolder);
		FolderTools.deleteFolder(actionsTestDataFolder, true);
		FolderTools.createFolder(actionsTestDataFolder);
		FolderTools.createFolder(actionsTestConfDataFolder);
		FolderTools.createFolder(actionsTestDefDataFolder);
		FolderTools.createFolder(actionsTestDataDataFolder);
		
		// Fwk configuration
		FolderTools.copyFromFolderToFolder(testFwkConfigurationHome, versionHomeConfFolder, false);
		
		// Data
		FolderTools.copyFromFolderToFolder(testDataHome, actionsTestDataDataFolder, true);
		
		// Definitions
		FolderTools.copyFromFolderToFolder(testDefConfigurationHome + File.separator + "connections", actionsTestDefDataFolder, false);
		FileTools.delete(actionsTestConfDataFolder + File.separator + ".gitkeep");
		FolderTools.copyFromFolderToFolder(testDefConfigurationHome + File.separator + "environments", actionsTestDefDataFolder, false);
		FileTools.delete(actionsTestConfDataFolder + File.separator + ".gitkeep");
		
		// Configurations
		FolderTools.copyFromFolderToFolder(testConfigurationHome, actionsTestConfDataFolder, false);
		FileTools.delete(actionsTestConfDataFolder + File.separator + ".gitkeep");
		
		// Create repository
		List<LaunchArgument> metadataCreateArgs = new ArrayList();
		LaunchArgument ini = new LaunchArgument(true,"-ini","iesi-test.ini");
		metadataCreateArgs.add(ini);
		LaunchArgument exit = new LaunchArgument(true,"-exit","false");
		metadataCreateArgs.add(exit);
		LaunchArgument create = new LaunchArgument(false,"-create","");
		metadataCreateArgs.add(create);
		LaunchArgument type = new LaunchArgument(true,"-type","general");		
		metadataCreateArgs.add(type);
		Launcher.execute("metadata",metadataCreateArgs);

		File[] confs = null;
		// Load definitions tests
		confs = FolderTools
				.getFilesInFolder(actionsTestDefDataFolder, "regex", ".+\\.yml");
		
		List<LaunchArgument> inputArgs = new ArrayList();
		inputArgs.add(ini);
		inputArgs.add(exit);
		LaunchArgument load = new LaunchArgument(false,"-load","");
		inputArgs.add(load);
		inputArgs.add(type);

		for (final File conf : confs) {
			FileTools.copyFromFileToFile(conf.getAbsolutePath(), metadataInNewFolder + File.separator + conf.getName() );
			LaunchArgument files = new LaunchArgument(true,"-files",conf.getAbsolutePath());
			inputArgs.add(files);
			Launcher.execute("metadata",inputArgs);
			inputArgs.remove(files);
		}

		
		// Load action tests
		confs = FolderTools
				.getFilesInFolder(actionsTestConfDataFolder, "regex", ".+\\.yml");
		
		inputArgs.add(ini);
		inputArgs.add(exit);
		inputArgs.add(load);
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
		LaunchArgument script = null;

		LaunchItemOperation launchItemOperation = new LaunchItemOperation(testLaunchConfigurationHome + File.separator + "actions.json");
		ObjectMapper objectMapper = new ObjectMapper();
		for (DataObject dataObject : launchItemOperation.getDataObjects()) {
			LaunchItem launchItem = objectMapper.convertValue(dataObject.getData(), LaunchItem.class);
			script = new LaunchArgument(true,"-script",launchItem.getScript());
			scriptInputArgs.add(script);
			
			// Parameter list
			LaunchArgument paramList = null;
			if (launchItem.getParameterList() != null && !launchItem.getParameterList().trim().isEmpty()) {
				paramList = new LaunchArgument(true,"-paramlist",launchItem.getParameterList());
				scriptInputArgs.add(paramList);
			}
			
			Launcher.execute("script",scriptInputArgs);

			scriptInputArgs.remove(script);
			scriptInputArgs.remove(paramList);
		}
		
	}
}