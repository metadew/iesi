package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.*;

public class MetadataRepositoryOperation {


    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors

    public MetadataRepositoryOperation() {
    }

    // Methods
//    public void cleanAllTables() {
//        LOGGER.info("metadata.clean.start");
//        LOGGER.trace("metadata.clean.query=" + "");
//        this.getMetadataRepository().cleanAllTables();
//        LOGGER.info("metadata.clean.end");
//
//    }

    // Drop the metadata data store
//    public void drop() {
//        this.dropAllTables();
//    }

//    public void dropAllTables() {
//        LOGGER.info("metadata.drop.start");
//        this.getMetadataRepository().dropAllTables(FrameworkLog.getInstance());
//        LOGGER.info("metadata.drop.end");
//
//    }

    // Create the metadata data store
//    public void create(boolean generateDdl) {
//        this.setAction("create");
//        this.setGenerateDdl(generateDdl);
//        this.getMetadataRepository().createAllTables();
//    }

    public void loadMetadataRepository(List<MetadataRepository> metadataRepositoryList) {
        this.loadMetadataRepository(metadataRepositoryList, "");
    }

    public void loadMetadataRepository(List<MetadataRepository> metadataRepositories, String input) {
        LOGGER.info("metadata.load.start");

        // Folder definition
        String inputFolder = FrameworkConfiguration.getInstance()
                .getMandatoryFrameworkFolder("metadata.in.new")
                .getAbsolutePath().toString();
        String workFolder = FrameworkConfiguration.getInstance()
                .getMandatoryFrameworkFolder("metadata.in.work")
                .getAbsolutePath().toString();
        String errorFolder = FrameworkConfiguration.getInstance()
                .getMandatoryFrameworkFolder("metadata.in.error")
                .getAbsolutePath().toString();
        String archiveFolder = FrameworkConfiguration.getInstance()
                .getMandatoryFrameworkFolder("metadata.in.done")
                .getAbsolutePath().toString();

        System.out.println(inputFolder);

        // Load files
        if (input.trim().equalsIgnoreCase("")) {
            this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, ".+\\.json");
            this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, ".+\\.yml");
        } else {
            if (ParsingTools.isRegexFunction(input)) {
                this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder,
                        ParsingTools.getRegexFunctionValue(input));
            } else {
                List<String> fileList = new ArrayList<>(Arrays.asList(input.split(",")));
                // ListTools.convertStringList(input, ",");
                for (String file : fileList) {
                    this.loadConfigurationItem(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, file);
                }
            }
        }

        LOGGER.info("metadata.load.end");

    }

    // Load a single file
    private void loadConfigurationItem(List<MetadataRepository> metadataRepositoryList, String inputFolder, String workFolder, String archiveFolder, String errorFolder,
                                       String inputFileName) {
        File file = new File(FilenameUtils.normalize(inputFolder + File.separator + inputFileName));
        this.loadConfigurationFile(metadataRepositoryList, file, inputFolder, workFolder, archiveFolder, errorFolder);
    }

    // Load entire folder
    @SuppressWarnings("unused")
    private void loadConfigurationSelection(List<MetadataRepository> metadataRepositories, String inputFolder, String workFolder, String archiveFolder,
                                            String errorFolder, String[] files) {
        for (String file : files) {
            File activeFile = new File(FilenameUtils.normalize(inputFolder + File.separator + file));
            this.loadConfigurationFile(metadataRepositories, activeFile, inputFolder, workFolder, archiveFolder, errorFolder);
        }
    }

    // Load entire folder
    private void loadConfigurationSelection(List<MetadataRepository> metadataRepositories, String inputFolder, String workFolder, String archiveFolder,
                                            String errorFolder, String regex) {
        final File folder = new File(FilenameUtils.normalize(inputFolder));
        final String file_filter = regex;
        final File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.matches(file_filter);
            }
        });

        if (files != null) {
            for (final File file : files) {
                this.loadConfigurationFile(metadataRepositories, file, inputFolder, workFolder, archiveFolder, errorFolder);
            }
        }
    }

    private void loadConfigurationFile(List<MetadataRepository> metadataRepositories, File file, String inputFolder, String workFolder, String archiveFolder,
                                       String errorFolder) {

        UUID uuid = UUID.randomUUID();

        boolean moveToWorkFolder = false;
        boolean moveToArchiveFolder = false;
        boolean moveToErrorFolder = false;

        if (!workFolder.trim().equalsIgnoreCase(""))
            moveToWorkFolder = true;

        if (moveToWorkFolder) {
            FileTools.copyFromFileToFile(FilenameUtils.normalize(inputFolder + File.separator + file.getName()),
                    FilenameUtils.normalize(workFolder + File.separator + file.getName()));
            FileTools.delete((FilenameUtils.normalize(inputFolder + File.separator + file.getName())));
        } else {
            workFolder = inputFolder;
        }

        if (!archiveFolder.trim().equalsIgnoreCase(""))
            moveToArchiveFolder = true;
        if (!errorFolder.trim().equalsIgnoreCase(""))
            moveToErrorFolder = true;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

        File workFile = new File(FilenameUtils.normalize(workFolder + File.separator + file.getName()));
        if (!workFile.isDirectory()) {
            LOGGER.info("metadata.file=" + file.getName());
            DataObjectOperation dataObjectOperation = new DataObjectOperation(workFile.getAbsolutePath());
            dataObjectOperation.saveToMetadataRepository(metadataRepositories);

            // Move file to archive folder
            if (moveToArchiveFolder) {
                String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
                        + uuid + "-" + workFile.getName();
                FileTools.copyFromFileToFile(workFile.getAbsolutePath(),
                        FilenameUtils.normalize(archiveFolder + File.separator + archiveFileName));
                FileTools.delete(workFile.getAbsolutePath());
            }

        }

    }

//    private void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {
//
//        boolean moveToArchiveFolder = false;
//        boolean moveToErrorFolder = false;
//
//        if (!archiveFolder.trim().equalsIgnoreCase(""))
//            moveToArchiveFolder = true;
//        if (!errorFolder.trim().equalsIgnoreCase(""))
//            moveToErrorFolder = true;
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
//
//        if (file.isDirectory()) {
//            // Ignore
//        } else {
//            try {
//                LOGGER.info("metadata.file=" + file.getName());
//                DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getMetadataRepository(), file.getAbsolutePath());
//                dataObjectOperation.saveToMetadataRepository();
//
//                // Move file to archive folder
//                if (moveToArchiveFolder) {
//                    String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
//                            + uuid + "-" + file.getName();
//                    FileTools.copyFromFileToFile(file.getAbsolutePath(),
//                            archiveFolder + File.separator + archiveFileName);
//                    FileTools.delete(file.getAbsolutePath());
//                }
//
//            } catch (Exception e) {
//
//                // Move file to archive folder
//                if (moveToErrorFolder) {
//                    String errorFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
//                            + uuid + "-" + file.getName();
//                    FileTools.copyFromFileToFile(file.getAbsolutePath(), errorFolder + File.separator + errorFileName);
//                    FileTools.delete(file.getAbsolutePath());
//                }
//
//            }
//        }
//
//    }

//    private void saveMetadataRepositoryDDL(String ddl) {
//        StringBuilder targetFilePath = new StringBuilder();
//        targetFilePath.append(FrameworkConfiguration.getInstance().getFrameworkFolder(.getInstance().getFolderAbsolutePath("metadata.out.ddl"));
//        targetFilePath.append(File.separator);
//        targetFilePath.append(this.getMetadataRepository().getName());
//        targetFilePath.append("_");
//        targetFilePath.append(this.getMetadataRepository().getCategory());
//        targetFilePath.append("_");
//        targetFilePath.append("create");
//        targetFilePath.append(".ddl");
//        FileTools.delete(targetFilePath.toString());
//        FileTools.appendToFile(targetFilePath.toString(), "", ddl);
//    }


}