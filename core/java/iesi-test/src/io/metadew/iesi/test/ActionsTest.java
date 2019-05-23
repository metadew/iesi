package io.metadew.iesi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.test.launch.LaunchArgument;
import io.metadew.iesi.test.launch.LaunchItem;
import io.metadew.iesi.test.launch.LaunchItemOperation;
import io.metadew.iesi.test.launch.Launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ActionsTest {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {

        Options options = new Options()
                .addOption(Option.builder("repository")
                        .hasArg()
                        .required()
                        .desc("Absolute location of the iesi repository")
                        .build())
                .addOption(Option.builder("sandbox")
                        .hasArg()
                        .required()
                        .desc("Absolute location of the iesi sandbox")
                        .build())
                .addOption(Option.builder("instance")
                        .hasArg()
                        .required()
                        .desc("Name of the iesi instance")
                        .build())
                .addOption(Option.builder("version")
                        .hasArg()
                        .required()
                        .desc("Version of the iesi instance")
                        .build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String repository = cmd.getOptionValue("repository");
            String sandbox = cmd.getOptionValue("sandbox");
            String instance = cmd.getOptionValue("instance");
            String version = cmd.getOptionValue("version");

            String repositoryHome = repository;
            @SuppressWarnings("unused")
            String sandboxHome = sandbox;
            String instanceHome = sandbox + File.separator + instance;
            String versionHome = instanceHome + File.separator + version;

            String testDataHome = repositoryHome + File.separator + "test" + File.separator + "data";
            String testDockerHome = repositoryHome + File.separator + "test" + File.separator + "docker";
            String testFwkConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
                    + File.separator + "conf" + File.separator + "fwk";
            String testLaunchConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
                    + File.separator + "conf" + File.separator + "launch";
            String testSetupConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
                    + File.separator + "conf" + File.separator + "setup";
            String testDefConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
                    + File.separator + "conf" + File.separator + "def";
            String actionsTestConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
                    + File.separator + "conf" + File.separator + "actions";
            String connectionsTestConfigurationHome = repositoryHome + File.separator + "test" + File.separator + "metadata"
                    + File.separator + "conf" + File.separator + "connections";


            String versionHomeConfFolder = versionHome + File.separator + "conf";
            String testDataFolder = versionHome + File.separator + "data" + File.separator + "iesi-test";
            String frameworkTestDataFolder = testDataFolder + File.separator + "fwk";
            String setupTestConfDataFolder = frameworkTestDataFolder + File.separator + "setup";
            String actionsTestConfDataFolder = frameworkTestDataFolder + File.separator + "actions";
            String connectionsTestConfDataFolder = frameworkTestDataFolder + File.separator + "connections";
            String actionsTestDefDataFolder = frameworkTestDataFolder + File.separator + "def";
            String fwkTestDataFolder = frameworkTestDataFolder + File.separator + "data";

            String dockerTestDataFolder = testDataFolder + File.separator + "docker";
            
            String metadataInNewFolder = versionHome + File.separator + "metadata" + File.separator + "in" + File.separator + "new";

            FolderTools.createFolder(testDataFolder);
            FolderTools.deleteFolder(frameworkTestDataFolder, true);
            FolderTools.deleteFolder(dockerTestDataFolder, true);
            FolderTools.createFolder(frameworkTestDataFolder);
            FolderTools.createFolder(setupTestConfDataFolder);
            FolderTools.createFolder(actionsTestConfDataFolder);
            FolderTools.createFolder(connectionsTestConfDataFolder);
            FolderTools.createFolder(actionsTestDefDataFolder);
            FolderTools.createFolder(fwkTestDataFolder);

            FolderTools.createFolder(dockerTestDataFolder);

            // Docker configuration
            FolderTools.copyFromFolderToFolder(testDockerHome + File.separator + "linux-ssh", dockerTestDataFolder + File.separator + "linux-ssh", true);
            FolderTools.copyFromFolderToFolder(testSetupConfigurationHome, setupTestConfDataFolder, false);

            // Fwk configuration
            FolderTools.copyFromFolderToFolder(testFwkConfigurationHome, versionHomeConfFolder, false);

            // Data
            FolderTools.copyFromFolderToFolder(testDataHome, fwkTestDataFolder, true);

            // Definitions
            FolderTools.copyFromFolderToFolder(testDefConfigurationHome + File.separator + "connections", actionsTestDefDataFolder, false);
            FileTools.delete(actionsTestConfDataFolder + File.separator + ".gitkeep");
            FolderTools.copyFromFolderToFolder(testDefConfigurationHome + File.separator + "environments", actionsTestDefDataFolder, false);
            FileTools.delete(actionsTestConfDataFolder + File.separator + ".gitkeep");

            // Configurations
            FolderTools.copyFromFolderToFolder(actionsTestConfigurationHome, actionsTestConfDataFolder, false);
            FileTools.delete(actionsTestConfDataFolder + File.separator + ".gitkeep");
            FolderTools.copyFromFolderToFolder(connectionsTestConfigurationHome, connectionsTestConfDataFolder, false);
            FileTools.delete(connectionsTestConfDataFolder + File.separator + ".gitkeep");

            // Create repository
            List<LaunchArgument> metadataCreateArgs = new ArrayList();
            LaunchArgument ini = new LaunchArgument(true, "-ini", "iesi-test.ini");
            metadataCreateArgs.add(ini);
            LaunchArgument exit = new LaunchArgument(true, "-exit", "false");
            metadataCreateArgs.add(exit);
            LaunchArgument create = new LaunchArgument(false, "-create", "");
            metadataCreateArgs.add(create);
            LaunchArgument type = new LaunchArgument(true, "-type", "general");
            metadataCreateArgs.add(type);
            Launcher.execute("metadata", metadataCreateArgs);

            File[] confs = null;
            // Load definitions tests
            confs = FolderTools
                    .getFilesInFolder(actionsTestDefDataFolder, "regex", ".+\\.yml");            
            
            List<LaunchArgument> inputArgs = new ArrayList();
            inputArgs.add(ini);
            inputArgs.add(exit);
            LaunchArgument load = new LaunchArgument(false, "-load", "");
            inputArgs.add(load);
            inputArgs.add(type);

            for (final File conf : confs) {
                FileTools.copyFromFileToFile(conf.getAbsolutePath(), metadataInNewFolder + File.separator + conf.getName());
                LaunchArgument files = new LaunchArgument(true, "-files", conf.getAbsolutePath());
                inputArgs.add(files);
                Launcher.execute("metadata", inputArgs);
                inputArgs.remove(files);
            }

            // Load setup scripts
            confs = FolderTools
                    .getFilesInFolder(setupTestConfDataFolder, "regex", ".+\\.yml");

            inputArgs.add(ini);
            inputArgs.add(exit);
            inputArgs.add(load);
            inputArgs.add(type);

            for (final File conf : confs) {
                FileTools.copyFromFileToFile(conf.getAbsolutePath(), metadataInNewFolder + File.separator + conf.getName());
                LaunchArgument files = new LaunchArgument(true, "-files", conf.getAbsolutePath());
                inputArgs.add(files);
                Launcher.execute("metadata", inputArgs);
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
                FileTools.copyFromFileToFile(conf.getAbsolutePath(), metadataInNewFolder + File.separator + conf.getName());
                LaunchArgument files = new LaunchArgument(true, "-files", conf.getAbsolutePath());
                inputArgs.add(files);
                Launcher.execute("metadata", inputArgs);
                inputArgs.remove(files);
            }

            
            // Load connections tests
            confs = FolderTools
                    .getFilesInFolder(connectionsTestConfDataFolder, "regex", ".+\\.yml");

            inputArgs.add(ini);
            inputArgs.add(exit);
            inputArgs.add(load);
            inputArgs.add(type);

            for (final File conf : confs) {
                FileTools.copyFromFileToFile(conf.getAbsolutePath(), metadataInNewFolder + File.separator + conf.getName());
                LaunchArgument files = new LaunchArgument(true, "-files", conf.getAbsolutePath());
                inputArgs.add(files);
                Launcher.execute("metadata", inputArgs);
                inputArgs.remove(files);
            }
            
            //------------

            List<LaunchArgument> scriptInputArgs = new ArrayList();
            scriptInputArgs.add(ini);
            scriptInputArgs.add(exit);
            LaunchArgument env = new LaunchArgument(true, "-env", "iesi-test");
            scriptInputArgs.add(env);
            LaunchArgument script = null;
            ObjectMapper objectMapper = new ObjectMapper();
            
            // Run initializations
            LaunchItemOperation launchInitializationOperation = new LaunchItemOperation(testLaunchConfigurationHome + File.separator + "initializations.json");
            for (DataObject dataObject : launchInitializationOperation.getDataObjects()) {
                LaunchItem launchItem = objectMapper.convertValue(dataObject.getData(), LaunchItem.class);
                script = new LaunchArgument(true, "-script", launchItem.getScript());
                scriptInputArgs.add(script);

                // Parameter list
                LaunchArgument paramList = null;
                if (launchItem.getParameterList() != null && !launchItem.getParameterList().trim().isEmpty()) {
                    paramList = new LaunchArgument(true, "-paramlist", launchItem.getParameterList());
                    scriptInputArgs.add(paramList);
                }

                Launcher.execute("script", scriptInputArgs);

                scriptInputArgs.remove(script);
                scriptInputArgs.remove(paramList);
            }
 
            
            // Run action tests
            LaunchItemOperation launchItemOperation = new LaunchItemOperation(testLaunchConfigurationHome + File.separator + "actions.json");
            for (DataObject dataObject : launchItemOperation.getDataObjects()) {
                LaunchItem launchItem = objectMapper.convertValue(dataObject.getData(), LaunchItem.class);
                script = new LaunchArgument(true, "-script", launchItem.getScript());
                scriptInputArgs.add(script);

                // Parameter list
                LaunchArgument paramList = null;
                if (launchItem.getParameterList() != null && !launchItem.getParameterList().trim().isEmpty()) {
                    paramList = new LaunchArgument(true, "-paramlist", launchItem.getParameterList());
                    scriptInputArgs.add(paramList);
                }

                Launcher.execute("script", scriptInputArgs);

                scriptInputArgs.remove(script);
                scriptInputArgs.remove(paramList);
            }
            
            // Run terminations
            LaunchItemOperation launchTerminationOperation = new LaunchItemOperation(testLaunchConfigurationHome + File.separator + "terminations.json");
            for (DataObject dataObject : launchTerminationOperation.getDataObjects()) {
                LaunchItem launchItem = objectMapper.convertValue(dataObject.getData(), LaunchItem.class);
                script = new LaunchArgument(true, "-script", launchItem.getScript());
                scriptInputArgs.add(script);

                // Parameter list
                LaunchArgument paramList = null;
                if (launchItem.getParameterList() != null && !launchItem.getParameterList().trim().isEmpty()) {
                    paramList = new LaunchArgument(true, "-paramlist", launchItem.getParameterList());
                    scriptInputArgs.add(paramList);
                }

                Launcher.execute("script", scriptInputArgs);

                scriptInputArgs.remove(script);
                scriptInputArgs.remove(paramList);
            }
            
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        
        System.exit(0);
    }
}