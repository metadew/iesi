package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.common.list.ListTools;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MetadataRepositoryOperation {

    private FrameworkExecution frameworkExecution;
    private MetadataRepository metadataRepository;
    private String action;
    private boolean generateDdl;

    // Constructors

    public MetadataRepositoryOperation(FrameworkExecution frameworkExecution,
                                       MetadataRepository metadataRepository) {
        this.setFrameworkExecution(frameworkExecution);
        this.setMetadataRepository(metadataRepository);
    }

    // Methods
    public void cleanAllTables() {
        this.getFrameworkExecution().getFrameworkLog().log("metadata.clean.start", Level.INFO);
        this.getFrameworkExecution().getFrameworkLog().log("metadata.clean.query=" + "", Level.TRACE);
        this.getMetadataRepository().cleanAllTables();
        this.getFrameworkExecution().getFrameworkLog().log("metadata.clean.end", Level.INFO);

    }

    // Drop the metadata data store
    public void drop() {
        this.dropAllTables();
    }

    public void dropAllTables() {
        this.getFrameworkExecution().getFrameworkLog().log("metadata.drop.start", Level.INFO);
        this.getMetadataRepository().dropAllTables(frameworkExecution.getFrameworkLog());
        this.getFrameworkExecution().getFrameworkLog().log("metadata.drop.end", Level.INFO);

    }

    // Create the metadata data store
    public void create(boolean generateDdl) {
        this.setAction("create");
        this.setGenerateDdl(generateDdl);
        this.getMetadataRepository().createAllTables();
    }

    public void loadMetadataRepository(List<MetadataRepository> metadataRepositoryList) {
        this.loadMetadataRepository(metadataRepositoryList, "");
    }

    public void loadMetadataRepository(List<MetadataRepository> metadataRepositories, String input) {
        this.getFrameworkExecution().getFrameworkLog().log("metadata.load.start", Level.INFO);

        // Folder definition
        String inputFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
                .getFolderConfiguration().getFolderAbsolutePath("metadata.in.new"));
        String workFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
                .getFolderConfiguration().getFolderAbsolutePath("metadata.in.work"));
        String errorFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
                .getFolderConfiguration().getFolderAbsolutePath("metadata.in.error"));
        String archiveFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
                .getFolderConfiguration().getFolderAbsolutePath("metadata.in.done"));

        // Load files
        if (input.trim().equalsIgnoreCase("")) {
            this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, ".+\\.json");
            this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, ".+\\.yml");
        } else {
            if (ParsingTools.isRegexFunction(input)) {
                this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder,
                        ParsingTools.getRegexFunctionValue(input));
            } else {
                List<String> fileList = ListTools.convertStringList(input, ",");
                for (String file : fileList) {
                    this.loadConfigurationItem(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, file);
                }
            }
        }

        this.getFrameworkExecution().getFrameworkLog().log("metadata.load.end", Level.INFO);

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

        for (final File file : files) {
            this.loadConfigurationFile(metadataRepositories, file, inputFolder, workFolder, archiveFolder, errorFolder);
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
            try {
                this.getFrameworkExecution().getFrameworkLog().log("metadata.file=" + file.getName(), Level.INFO);
                DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), metadataRepositories, workFile.getAbsolutePath());
                dataObjectOperation.saveToMetadataRepository();

                // Move file to archive folder
                if (moveToArchiveFolder) {
                    String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
                            + uuid + "-" + workFile.getName();
                    FileTools.copyFromFileToFile(workFile.getAbsolutePath(),
                            FilenameUtils.normalize(archiveFolder + File.separator + archiveFileName));
                    FileTools.delete(workFile.getAbsolutePath());
                }

            } catch (Exception e) {

                // Move file to error folder
                if (moveToErrorFolder) {
                    String errorFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
                            + uuid + "-" + file.getName();
                    FileTools.copyFromFileToFile(workFile.getAbsolutePath(),
                            FilenameUtils.normalize(errorFolder + File.separator + errorFileName));
                    FileTools.delete(workFile.getAbsolutePath());
                }

            }
        }

    }

    @SuppressWarnings("unused")
    private void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {

        boolean moveToArchiveFolder = false;
        boolean moveToErrorFolder = false;

        if (!archiveFolder.trim().equalsIgnoreCase(""))
            moveToArchiveFolder = true;
        if (!errorFolder.trim().equalsIgnoreCase(""))
            moveToErrorFolder = true;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

        if (file.isDirectory()) {
            // Ignore
        } else {
            try {
                this.getFrameworkExecution().getFrameworkLog().log("metadata.file=" + file.getName(), Level.INFO);
                DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(),
                        this.getMetadataRepository(), file.getAbsolutePath());
                dataObjectOperation.saveToMetadataRepository();

                // Move file to archive folder
                if (moveToArchiveFolder) {
                    String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
                            + uuid + "-" + file.getName();
                    FileTools.copyFromFileToFile(file.getAbsolutePath(),
                            archiveFolder + File.separator + archiveFileName);
                    FileTools.delete(file.getAbsolutePath());
                }

            } catch (Exception e) {

                // Move file to archive folder
                if (moveToErrorFolder) {
                    String errorFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
                            + uuid + "-" + file.getName();
                    FileTools.copyFromFileToFile(file.getAbsolutePath(), errorFolder + File.separator + errorFileName);
                    FileTools.delete(file.getAbsolutePath());
                }

            }
        }

    }

    @SuppressWarnings("unused")
    private void saveMetadataRepositoryDDL(String ddl) {
        StringBuilder targetFilePath = new StringBuilder();
        targetFilePath.append(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
                .getFolderAbsolutePath("metadata.out.ddl"));
        targetFilePath.append(File.separator);
        targetFilePath.append(this.getMetadataRepository().getName());
        targetFilePath.append("_");
        targetFilePath.append(this.getMetadataRepository().getCategory());
        targetFilePath.append("_");
        targetFilePath.append("create");
        targetFilePath.append(".ddl");
        FileTools.delete(targetFilePath.toString());
        FileTools.appendToFile(targetFilePath.toString(), "", ddl);
    }

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isGenerateDdl() {
        return generateDdl;
    }

    public void setGenerateDdl(boolean generateDdl) {
        this.generateDdl = generateDdl;
    }
}