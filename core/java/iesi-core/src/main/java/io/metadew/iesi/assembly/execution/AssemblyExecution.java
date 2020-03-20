package io.metadew.iesi.assembly.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.assembly.operation.FileSystemOperation;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.action.type.ActionType;
import io.metadew.iesi.metadata.definition.component.ComponentType;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import io.metadew.iesi.metadata.definition.dataframe.DataframeItemType;
import io.metadew.iesi.metadata.definition.dataframe.DataframeType;
import io.metadew.iesi.metadata.definition.generation.*;
import io.metadew.iesi.metadata.definition.ledger.LedgerType;
import io.metadew.iesi.metadata.definition.script.type.ScriptType;
import io.metadew.iesi.metadata.definition.subroutine.SubroutineType;
import io.metadew.iesi.metadata.definition.user.UserType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class AssemblyExecution {

    private final FileSystemOperation fileSystemOperation;

    private String repository;
    private String development;
    private String sandbox;
    private String instance;
    private String version;
    private String configuration;

    private boolean applyConfiguration;
    private boolean testAssembly;
    private boolean distribution;

    private final static Logger LOGGER = LogManager.getLogger();

    public AssemblyExecution(String repository, String development, String sandbox, String instance, String version,
                             String configuration, boolean applyConfiguration, boolean testAssembly, boolean distribution) {
        this.repository = repository;
        this.development = development;
        this.sandbox = sandbox;
        this.instance = instance;
        this.version = version;
        this.configuration = configuration;
        this.applyConfiguration = applyConfiguration;
        this.testAssembly = testAssembly;
        this.distribution = distribution;
        this.fileSystemOperation = new FileSystemOperation();
    }

    // Methods
    public void execute() throws IOException {
        String instanceHome = sandbox + File.separator + instance;
        String versionHome = instanceHome + File.separator + version;
        String configurationHome = sandbox + File.separator + "conf" + File.separator + instance + File.separator + configuration;

        deleteVersionDirectory(versionHome);
        recreateVersionDirectory(instanceHome, versionHome);

        createIESISkeleton(versionHome);

        loadLicenses(versionHome);
        loadMavenDependencies(versionHome);

        loadRestLicenses(versionHome);
        loadRestDependencies(versionHome);

        //loadMetadataConfiguration();
        //loadMetadataDefinitions();

        // loadSystemConfigurations();
        loadAssets(versionHome);
        loadAssetsNodist(versionHome);


        // Load configuration into directory structure
        if (this.isApplyConfiguration()) {
            FolderTools.copyFromFolderToFolder(configurationHome, versionHome, true);
        }

        // Load test assets
        if (this.isTestAssembly()) {
            String testConfigHome = repository + File.separator + "test" + File.separator + "conf";
            String versionMetadataInput = versionHome + File.separator + "metadata" + File.separator + "in" + File.separator + "new";
            FolderTools.copyFromFolderToFolder(testConfigHome, versionMetadataInput, false);
        }


        //initConfiguration(versionHome);
        initFramework(versionHome);
        // create folder
        /*
         * String metadataDefHome = versionHome + File.separator + "metadata" +
         * File.separator + "def"; String runExecHome = versionHome + File.separator +
         * "run" + File.separator + "exec";
         * this.getAssemblyContext().getConfigTools().addSetting(
         * this.getAssemblyContext().getConfigTools().getSettingsConfig().getSettingPath
         * ("metadata.repository.type"), "SQLITE"); SqliteDatabaseConnection
         * executionServerRepositoryConnection = new SqliteDatabaseConnection(
         * runExecHome + File.separator + "ExecutionServerRepository.db3");
         * DataObjectOperation executionServerTablesOperation = new
         * DataObjectOperation(null, metadataDefHome + File.separator +
         * "ExecutionServerTables.json"); MetadataRepositoryConfigurationBack
         * metadataRepositoryConfiguration = new MetadataRepositoryConfigurationBack(
         * this.getAssemblyContext().getConfigTools(),
         * executionServerRepositoryConnection);
         *
         * ObjectMapper objectMapper = new ObjectMapper(); for (DataObject
         * executionServerTable : executionServerTablesOperation.getDataObjects()) {
         * String output = "";
         *
         * // Metadata Tables if
         * (executionServerTable.getType().equalsIgnoreCase("metadatatable")) {
         * MetadataTable metadataTable =
         * objectMapper.convertValue(executionServerTable.getData(),
         * MetadataTable.class); MetadataTableConfiguration metadataTableConfiguration =
         * new MetadataTableConfiguration( metadataTable,
         * metadataRepositoryConfiguration); output =
         * metadataTableConfiguration.getCreateStatement(); }
         *
         * InputStream inputStream = FileTools .convertToInputStream(output,
         * this.getAssemblyContext().getConfigTools());
         * executionServerRepositoryConnection.executeScript(inputStream); }
         */
    }

    private void initFramework(String versionHome) {

    }

    private void initConfiguration(String versionHome) throws JsonProcessingException {
        LOGGER.info(MessageFormat.format("Initialize configuration into version home: {0}", versionHome));
        // Init configuration
        // TODO optimize for configuration in yaml
        // Create Folders
        String metadataConfHome = versionHome + File.separator + "metadata" + File.separator + "conf";
        //FolderTools.createFolder(metadataConfHome + File.separator + "ActionType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "ComponentType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "ConnectionType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "GenerationType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "GenerationRuleType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "GenerationOutputType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "GenerationControlType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "GenerationControlRuleType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "SubroutineType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "ScriptType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "DataframeType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "DataframeItemType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "LedgerType");
//        FolderTools.createFolder(metadataConfHome + File.separator + "UserType");

//        final File[] confs = FolderTools.getFilesInFolder(metadataConfHome, "regex", ".+\\.json");
//        for (final File conf : confs) {
//            // Read configuration
//            DataObjectOperation dataObjectOperation = new DataObjectOperation(conf.getAbsolutePath());
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
//                DataObject dataObjectOutput = null;
//                String output;
//                String name = "";
//
//                // Action Types
//                if (dataObject.getType().equalsIgnoreCase("actiontype")) {
//                    ActionType actionType = objectMapper.convertValue(dataObject.getData(), ActionType.class);
//                    dataObjectOutput = new DataObject("ActionType", actionType);
//                    //name = actionType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("componenttype")) {
//                    ComponentType componentType = objectMapper.convertValue(dataObject.getData(),
//                            ComponentType.class);
//                    dataObjectOutput = new DataObject("ComponentType", componentType);
//                    name = componentType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("connectiontype")) {
//                    ConnectionType connectionType = objectMapper.convertValue(dataObject.getData(),
//                            ConnectionType.class);
//                    dataObjectOutput = new DataObject("ConnectionType", connectionType);
//                    // name = connectionType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("generationtype")) {
//                    GenerationType generationType = objectMapper.convertValue(dataObject.getData(),
//                            GenerationType.class);
//                    dataObjectOutput = new DataObject("GenerationType", generationType);
//                    name = generationType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("generationruletype")) {
//                    GenerationRuleType generationRuleType = objectMapper.convertValue(dataObject.getData(),
//                            GenerationRuleType.class);
//                    dataObjectOutput = new DataObject("GenerationRuleType", generationRuleType);
//                    name = generationRuleType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("generationoutputtype")) {
//                    GenerationOutputType generationOutputType = objectMapper.convertValue(dataObject.getData(),
//                            GenerationOutputType.class);
//                    dataObjectOutput = new DataObject("GenerationOutputType", generationOutputType);
//                    name = generationOutputType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("generationcontroltype")) {
//                    GenerationControlType generationControlType = objectMapper.convertValue(dataObject.getData(),
//                            GenerationControlType.class);
//                    dataObjectOutput = new DataObject("GenerationControlType", generationControlType);
//                    name = generationControlType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("generationcontrolruletype")) {
//                    GenerationControlRuleType generationControlRuleType = objectMapper
//                            .convertValue(dataObject.getData(), GenerationControlRuleType.class);
//                    dataObjectOutput = new DataObject("GenerationControlRuleType", generationControlRuleType);
//                    name = generationControlRuleType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("subroutinetype")) {
//                    SubroutineType subroutineType = objectMapper.convertValue(dataObject.getData(),
//                            SubroutineType.class);
//                    dataObjectOutput = new DataObject("SubroutineType", subroutineType);
//                    name = subroutineType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("scripttype")) {
//                    ScriptType scriptType = objectMapper.convertValue(dataObject.getData(), ScriptType.class);
//                    dataObjectOutput = new DataObject("ScriptType", scriptType);
//                    name = scriptType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("ledgertype")) {
//                    LedgerType ledgerType = objectMapper.convertValue(dataObject.getData(), LedgerType.class);
//                    dataObjectOutput = new DataObject("LedgerType", ledgerType);
//                    name = ledgerType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("dataframetype")) {
//                    DataframeType dataframeType = objectMapper.convertValue(dataObject.getData(),
//                            DataframeType.class);
//                    dataObjectOutput = new DataObject("DataframeType", dataframeType);
//                    name = dataframeType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("dataframeitemtype")) {
//                    DataframeItemType dataframeItemType = objectMapper.convertValue(dataObject.getData(),
//                            DataframeItemType.class);
//                    dataObjectOutput = new DataObject("DataframeItemType", dataframeItemType);
//                    name = dataframeItemType.getName();
//                } else if (dataObject.getType().equalsIgnoreCase("usertype")) {
//                    UserType userType = objectMapper.convertValue(dataObject.getData(), UserType.class);
//                    dataObjectOutput = new DataObject("UserType", userType);
//                    name = userType.getName();
//                }
//
//                // Write output
//                output = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataObjectOutput);
//                FileTools.appendToFile(metadataConfHome + File.separator + dataObject.getType() + File.separator + name + ".json", "", output);
//
//            }

        //}
    }

    private void loadAssetsNodist(String versionHome) throws IOException {
        LOGGER.info(MessageFormat.format("Loading assets (no dist) into version home: {0}", versionHome));
        // Load nodist assets into directory structure
        if (!this.isDistribution()) {
            String fileSystemConfigNoDist = development + File.separator + "core" + File.separator + "assembly" + File.separator + "file-assembly-nodist.conf";
            File file = new File(fileSystemConfigNoDist);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                String innerpart = readLine.trim();
                String[] parts = innerpart.split(";");
                // int assemblyCode = Integer.parseInt(parts[4]);

                // Take all items into account
                String sourcePath = parts[1].replace("#GIT_REPO#", repository);
                sourcePath = sourcePath.replace("#GIT_DEV#", development);
                String targetPath = versionHome + parts[2] + File.separator + parts[0];
                FileTools.copyFromFileToFile(sourcePath, targetPath);
            }
        }
    }

    private void loadAssets(String versionHome) throws IOException {
        LOGGER.info(MessageFormat.format("Loading assets into version home: {0}", versionHome));
        // Load assets into directory structure
        String fileSystemConfig = repository + File.separator + "core" + File.separator + "assembly" + File.separator + "file-assembly.conf";
        File file = new File(fileSystemConfig);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String readLine;
        while ((readLine = bufferedReader.readLine()) != null) {
            String innerpart = readLine.trim();
            String[] parts = innerpart.split(";");
            // int assemblyCode = Integer.parseInt(parts[4]);

            // Take all items into account
            String sourcePath = parts[1].replace("#GIT_REPO#", repository);
            sourcePath = sourcePath.replace("#GIT_DEV#", development);
            String targetPath = versionHome + parts[2] + File.separator + parts[0];
            LOGGER.debug("Copying " + sourcePath + " to " + targetPath);
            Files.copy(Paths.get(sourcePath), Paths.get(targetPath));
            //FileTools.copyFromFileToFile(sourcePath, targetPath);
        }
        bufferedReader.close();
    }

    private void loadSystemConfigurations() throws IOException {
        LOGGER.info("Loading system configurations");
        String metadataInitEditHome = repository + File.separator + "core" + File.separator + "sys" + File.separator + "init";
        final File[] inputInitFolders = FolderTools.getFilesInFolder(metadataInitEditHome, "all", "");
        for (final File inputConfFolder : inputInitFolders) {
            if (inputConfFolder.isDirectory()) {
                final File[] inputConfs = FolderTools.getFilesInFolder(inputConfFolder.getAbsolutePath(), "regex", ".+\\.yml");

                List<DataObject> confInitObjects = new ArrayList<>();
                for (final File inputConf : inputConfs) {
                    // Read configuration
                    DataObjectOperation inputInitObjectOperation = new DataObjectOperation(inputConf.getAbsolutePath());

                    ObjectMapper inputObjectMapper = new ObjectMapper();
                    confInitObjects.addAll(inputInitObjectOperation.getDataObjects());
                    String metadataInitFileName = inputConfFolder.getName() + ".json";
                    String metadataInitFilePath = metadataInitEditHome + File.separator + metadataInitFileName;

                    // Write file to github repository
                    inputObjectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(metadataInitFilePath), confInitObjects);

                    // Copy file to docs/_data for documentation update
                    FileTools.copyFromFileToFile(metadataInitFilePath, repository + File.separator + "docs" + File.separator + "_data" + File.separator + "framework" + File.separator + metadataInitFileName);
                }
            }
        }
    }

    private void loadMetadataDefinitions() throws IOException {
        LOGGER.info("Loading metadata definitions");
        String metadataDefEditHome = repository + File.separator + "core" + File.separator + "metadata" + File.separator + "def";
        final File[] inputDefFolders = FolderTools.getFilesInFolder(metadataDefEditHome, "all", "");
        for (final File inputDefFolder : inputDefFolders) {
            if (inputDefFolder.isDirectory()) {
                // Tables
                final File[] inputDefTables = FolderTools.getFilesInFolder(inputDefFolder.getAbsolutePath() + File.separator + "Tables", "regex", ".+\\.yml");
                List<DataObject> defTableDataObjects = new ArrayList<>();
                for (final File inputDef : inputDefTables) {
                    // Read configuration
                    LOGGER.debug("reading metadata definition " + inputDef.getName());
                    DataObjectOperation inputDataObjectOperation = new DataObjectOperation(inputDef.getAbsolutePath());

                    ObjectMapper inputObjectMapper = new ObjectMapper();
                    defTableDataObjects.addAll(inputDataObjectOperation.getDataObjects());
                    String metadataDefFileName = inputDefFolder.getName() + "Tables.json";
                    String metadataDefFilePath = metadataDefEditHome + File.separator + metadataDefFileName;

                    // Write file to github repository
                    inputObjectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(metadataDefFilePath), defTableDataObjects);

                    // Copy file to docs/_data for documentation update
                    FileTools.copyFromFileToFile(metadataDefFilePath, repository + File.separator + "docs" + File.separator + "_data" + File.separator + "datamodel" + File.separator + metadataDefFileName);
                }

                // Objects
                final File[] inputDefObjects = FolderTools.getFilesInFolder(inputDefFolder.getAbsolutePath() + File.separator + "Objects", "regex", ".+\\.yml");
                List<DataObject> defObjectDataObjects = new ArrayList<>();
                for (final File inputDef : inputDefObjects) {
                    // Read configuration
                    DataObjectOperation inputDataObjectOperation = new DataObjectOperation(inputDef.getAbsolutePath());

                    ObjectMapper inputObjectMapper = new ObjectMapper();
                    defObjectDataObjects.addAll(inputDataObjectOperation.getDataObjects());
                    String metadataDefFileName = inputDefFolder.getName() + "Objects.json";
                    String metadataDefFilePath = metadataDefEditHome + File.separator + metadataDefFileName;

                    // Write file to github repository
                    inputObjectMapper.writerWithDefaultPrettyPrinter().writeValue(
                            new File(metadataDefFilePath),
                            defObjectDataObjects);

                    // Copy file to docs/_data for documentation update
                    FileTools.copyFromFileToFile(metadataDefFilePath, repository + File.separator + "docs" + File.separator + "_data" + File.separator + "datamodel" + File.separator + metadataDefFileName);
                }

            }
        }
    }

    private void loadMetadataConfiguration() throws IOException {
        LOGGER.info("Loading metadata configurations");
        String metadataConfEditHome = repository + File.separator + "core" + File.separator + "metadata" + File.separator + "conf";
        final File[] inputConfFolders = FolderTools.getFilesInFolder(metadataConfEditHome, "all", "");
        for (final File inputConfFolder : inputConfFolders) {
            if (inputConfFolder.isDirectory()) {
                final File[] inputConfs = FolderTools.getFilesInFolder(inputConfFolder.getAbsolutePath(), "regex", ".+\\.yml");
                List<DataObject> confDataObjects = new ArrayList<>();
                for (final File inputConf : inputConfs) {
                    // Read configuration
                    DataObjectOperation inputDataObjectOperation = new DataObjectOperation(inputConf.getAbsolutePath());

                    ObjectMapper inputObjectMapper = new ObjectMapper();
                    confDataObjects.addAll(inputDataObjectOperation.getDataObjects());
                    String metadataConfFileName = inputConfFolder.getName() + "s.json";
                    String metadataConfFilePath = metadataConfEditHome + File.separator + metadataConfFileName;

                    // Write file to github repository
                    inputObjectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(metadataConfFilePath), confDataObjects);

                    // Copy file to docs/_data for documentation update
                    FileTools.copyFromFileToFile(metadataConfFilePath, repository + File.separator + "docs" + File.separator + "_data" + File.separator + metadataConfFileName);
                }
            }
        }
    }

    private void loadRestDependencies(String versionHome) throws IOException {
        LOGGER.info(MessageFormat.format("Loading dependencies (REST) into version home: {0}", versionHome));
        String mavenDependenciesSource = repository + File.separator + "core" + File.separator + "java" + File.separator + "iesi-rest-without-microservices" + File.separator + "target";
        String mavenDependenciesTarget = versionHome + File.separator + "rest";
        Path restJar = Files.walk(Paths.get(mavenDependenciesSource), 1)
                .filter(path -> path.getFileName().toString().endsWith("jar"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find REST jar"));
        Files.copy(restJar, Paths.get(mavenDependenciesTarget).resolve(restJar.getFileName()), REPLACE_EXISTING);
        // FolderTools.copyFromFolderToFolder(mavenDependenciesSource, mavenDependenciesTarget, true);
    }

    private void loadRestLicenses(String versionHome) {
        LOGGER.info(MessageFormat.format("Loading licenses (REST) into version home: {0}", versionHome));
        String licensesReportSource = repository + File.separator + "core" + File.separator + "java" + File.separator + "iesi-rest-without-microservices" + File.separator + "target" + File.separator + "site";
        String licensesReportTarget = versionHome + File.separator + "licenses" + File.separator + "rest";
        FolderTools.copyFromFolderToFolder(licensesReportSource, licensesReportTarget, true);
    }

    private void loadMavenDependencies(String versionHome) {
        LOGGER.info(MessageFormat.format("Loading rest into version home: {0}", versionHome));
        // Load maven dependencies
        String mavenDependenciesSource = repository + File.separator + "core" + File.separator + "java" + File.separator + "iesi-core" + File.separator + "target" + File.separator + "dependencies";
        String mavenDependenciesTarget = versionHome + File.separator + "lib";
        FolderTools.copyFromFolderToFolder(mavenDependenciesSource, mavenDependenciesTarget, true);
    }

    private void loadLicenses(String versionHome) {
        LOGGER.info(MessageFormat.format("Loading licenses into version home: {0}", versionHome));
        // Load Licenses
        String licensesSource = repository + File.separator + "licenses";
        String licensesTarget = versionHome + File.separator + "licenses";
        FolderTools.copyFromFolderToFolder(licensesSource, licensesTarget, true);

        String licensesReportSource = repository + File.separator + "core" + File.separator + "java"
                + File.separator + "iesi-core" + File.separator + "target" + File.separator + "site";
        String licensesReportTarget = versionHome + File.separator + "licenses" + File.separator + "core";
        FolderTools.copyFromFolderToFolder(licensesReportSource, licensesReportTarget, true);
    }

    private void createIESISkeleton(String versionHome) {
        LOGGER.info(MessageFormat.format("Creating IESI Skeleton at version home: {0}", versionHome));
        // Loop the file system configuration
        String fileSystemStructure = repository + File.separator + "core" + File.separator + "assembly" + File.separator + "folder-assembly.conf";
        fileSystemOperation.createSolutionStructure(fileSystemStructure, versionHome);
    }

    private void recreateVersionDirectory(String instanceHome, String versionHome) {
        LOGGER.info(MessageFormat.format("Recreating version home: {0}", versionHome));
        FolderTools.createFolder(instanceHome);
        FolderTools.createFolder(versionHome);
    }

    private void deleteVersionDirectory(String versionHome) throws IOException {
        if (Files.exists(Paths.get(versionHome))) {
            LOGGER.info(MessageFormat.format("Deleting version home: {0}", versionHome));
            Files.walk(Paths.get(versionHome))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }


    private boolean isTestAssembly() {
        return testAssembly;
    }

    private boolean isDistribution() {
        return distribution;
    }

    private boolean isApplyConfiguration() {
        return applyConfiguration;
    }

}