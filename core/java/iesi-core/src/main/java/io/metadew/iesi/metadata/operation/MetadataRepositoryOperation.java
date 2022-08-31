package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class MetadataRepositoryOperation {


    private static final Logger LOGGER = LogManager.getLogger();

    public MetadataRepositoryOperation() {
    }

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

        LOGGER.info(inputFolder);

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
        final File[] files = folder.listFiles((dir, name) -> name.matches(file_filter));

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
            DataObjectOperation dataObjectOperation = new DataObjectOperation(Paths.get(workFile.getAbsolutePath()));
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
}