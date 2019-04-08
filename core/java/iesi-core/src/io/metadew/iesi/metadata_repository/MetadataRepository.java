package io.metadew.iesi.metadata_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkLog;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata_repository.repository.Repository;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public abstract class MetadataRepository {

    String frameworkCode;
    Repository repository;
    String name;
    String scope;
    String instanceName;
    private HashMap<String, String> objects;
    private List<MetadataObject> metadataObjects;
    private List<MetadataTable> metadataTables;

    public MetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository,
                              String repositoryObjectsPath, String repositoryTablePath) {
        this.frameworkCode = frameworkCode;
        this.name = name;
        this.scope = scope;
        this.instanceName = instanceName;
        this.repository = repository;
        metadataObjects = new ArrayList<>();
        metadataTables = new ArrayList<>();

        DataObjectOperation dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(repositoryObjectsPath + File.separator + getObjectDefinitionFileName());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadataobject")) {
                metadataObjects.add(objectMapper.convertValue(dataObject.getData(), MetadataObject.class));
            }
        }

        dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(repositoryTablePath + File.separator + getDefinitionFileName());
        dataObjectOperation.parseFile();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadatatables")) {
                metadataTables.add(objectMapper.convertValue(dataObject.getData(), MetadataTable.class));
            }
        }

    }

    public abstract String getDefinitionFileName();

    public abstract String getObjectDefinitionFileName();

    public abstract String getCategory();

    public String getTableNamePrefix() {
        String frameworkCodeString = getFrameworkCode().map(frameworkCode -> frameworkCode + "_").orElse("");
        String instanceNameString = getInstanceName().map(instanceName -> instanceName + "_").orElse("");
        return frameworkCodeString + instanceNameString;
        }

    public Optional<String> getFrameworkCode() {
        return Optional.ofNullable(frameworkCode);
    }

    public void cleanAllTables(FrameworkLog frameworkLog) {
        frameworkLog.log("metadata.clean.start", Level.INFO);

        frameworkLog.log("metadata.clean.query=" + "", Level.TRACE);

        this.repository.cleanAllTables(getTableNamePrefix(), frameworkLog);

        frameworkLog.log("metadata.clean.end", Level.INFO);

    }

    public abstract String getCategoryPrefix();

    public List<String> getAllTables(FrameworkLog frameworkLog) {
        return repository.getAllTables(getTableNamePrefix());
    }

    public void dropAllTables(FrameworkLog frameworkLog) {
        repository.dropAllTables(getTableNamePrefix(), frameworkLog);
    }

    public CachedRowSet executeQuery(String query, String logonType) {
        return repository.executeQuery(query,logonType);
    }

    public void executeUpdate(String query) {
        repository.executeUpdate(query);
    }

    public void executeScript(String fileName, String logonType) {
        repository.executeScript(fileName, logonType);
    }

    public void executeScript(InputStream inputStream, String logonType) {
        repository.executeScript(inputStream, logonType);
    }

    public void executeScript(InputStream inputStream) {
        repository.executeScript(inputStream, "writer");
    }


    // Create the metadata data store
    public abstract void create(boolean generateDdl);
//    {
//        this.setAction("create");
//        this.setGenerateDdl(generateDdl);
//        if (this.getMetadataRepository().getGroup().equalsIgnoreCase("filestore")) {
//            MetadataFileStoreRepositoryImpl metadataFileStoreRepositoryImpl = new MetadataFileStoreRepositoryImpl(
//                    this.getFrameworkExecution());
//            metadataFileStoreRepositoryImpl.createStructure();
//        } else if (this.getMetadataRepository().getGroup().equalsIgnoreCase("database")) {
//            this.createAllTables();
//        } else {
//            throw new RuntimeException("metadata.repository.group.invalid");
//        }
//    }

    public void createTable(MetadataTable metadataTable) {

    }

    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    public abstract void createAllTables();
//    {
//        this.getFrameworkExecution().getFrameworkLog().log("metadata.create.start", Level.INFO);
//
//        final File folder = new File(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
//                .getFolderAbsolutePath("metadata.def"));
//        this.getFrameworkExecution().getFrameworkLog().log("metadata.create.folder=" + folder.getPath(), Level.INFO);
//
//        List<MetadataRepositoryConfigurationBack> metadataRepositoryConfigurationList = new ArrayList();
//        metadataRepositoryConfigurationList.add(this.getMetadataRepository());
//
//        // Select appropriate definition file
//        String[] files = null;
//        MetadataRepositoryCategoryConfiguration[] metadataRepositoryCategoryConfigurations = null;
//        String metadataRepositoryCategory = this.getMetadataRepository().getCategory();
//        if (metadataRepositoryCategory == null)
//            metadataRepositoryCategory = "";
//        if (metadataRepositoryCategory.equals("metadew")) {
//            files = new String[] { "MetadewTables.json" };
//        } else if (metadataRepositoryCategory.equals("connectivity")) {
//            files = new String[] { "ConnectivityTables.json" };
//        } else if (metadataRepositoryCategory.equals("control")) {
//            files = new String[] { "ControlTables.json" };
//        } else if (metadataRepositoryCategory.equals("design")) {
//            files = new String[] { "DesignTables.json" };
//        } else if (metadataRepositoryCategory.equals("trace")) {
//            files = new String[] { "TraceTables.json" };
//        } else if (metadataRepositoryCategory.equals("result")) {
//            files = new String[] { "ResultTables.json" };
//        } else if (metadataRepositoryCategory.equals("general")) {
//            files = new String[] { "ConnectivityTables.json", "ControlTables.json", "DesignTables.json",
//                    "ResultTables.json", "TraceTables.json" };
//            metadataRepositoryCategoryConfigurations = new MetadataRepositoryCategoryConfiguration[] {
//                    this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig()
//                            .getConnectivityMetadataRepository(),
//                    this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig()
//                            .getControlMetadataRepository(),
//                    this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig()
//                            .getDesignMetadataRepository(),
//                    this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig()
//                            .getResultMetadataRepository(),
//                    this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig()
//                            .getTraceMetadataRepository() };
//        } else {
//            files = new String[] { "ConnectivityTables.json", "DesignTables.json", "ResultTables.json",
//                    "TraceTables.json" };
//        }
//
//        this.loadConfigurationSelection(metadataRepositoryConfigurationList, this.getFrameworkExecution().getFrameworkConfiguration()
//                .getFolderConfiguration().getFolderAbsolutePath("metadata.def"), "", "", "", files);
//
//        this.getFrameworkExecution().getFrameworkLog().log("metadata.create.end", Level.INFO);
//
//    }

    public abstract void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid);
//    {
//
//        boolean moveToArchiveFolder = false;
//        boolean moveToErrorFolder = false;
//
//        if (!archiveFolder.trim().equals(""))
//            moveToArchiveFolder = true;
//        if (!errorFolder.trim().equals(""))
//            moveToErrorFolder = true;
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
//
//        if (file.isDirectory()) {
//            // Ignore
//        } else {
//            try {
//                this.getFrameworkExecution().getFrameworkLog().log("metadata.file=" + file.getName(), Level.INFO);
//                DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(),
//                        this.getMetadataRepository(), file.getAbsolutePath());
//                if (this.isGenerateDdl()) {
//                    this.saveMetadataRepositoryDDL(dataObjectOperation.getMetadataRepositoryDdl());
//                } else {
//                    dataObjectOperation.saveToMetadataRepository();
//                }
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

    private void saveMetadataRepositoryDDL(String ddl) {
//        StringBuilder targetFilePath = new StringBuilder();
//        targetFilePath.append(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
//                .getFolderAbsolutePath("metadata.out.ddl"));
//        targetFilePath.append(File.separator);
//        targetFilePath.append(this.getMetadataRepository().getName());
//        targetFilePath.append("_");
//        targetFilePath.append(this.getMetadataRepository().getCategory());
//        targetFilePath.append("_");
//        targetFilePath.append("create");
//        targetFilePath.append(".ddl");
//        FileTools.delete(targetFilePath.toString());
//        FileTools.appendToFile(targetFilePath.toString(), "", ddl);
    }

    public String getTableNameByLabel(String label) {
        return getMetadataTables().stream().filter(metadataTable -> metadataTable.getLabel().equalsIgnoreCase(label)).findFirst().get().getName();
    }

    public String getName() {
        return name;
    }

    public List<MetadataTable> getMetadataTables() {
        return metadataTables;
    }

    public List<MetadataObject> getMetadataObjects() {
        return metadataObjects;
    }

    public Repository getRepository() {
        return repository;
    }

    private Optional<String> getInstanceName() {
        return Optional.ofNullable(instanceName);
    }

    public abstract void save(DataObject dataObject, FrameworkExecution frameworkExecution);
}
