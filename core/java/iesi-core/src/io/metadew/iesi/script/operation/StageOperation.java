package io.metadew.iesi.script.operation;

import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Operation to manage stage items that have been defined in the script.
 *
 * @author peter.billen
 */
public class StageOperation {

    private FrameworkExecution frameworkExecution;
    private SqliteDatabaseConnection stageConnection;
    private String stageName;
    private String stageFileName;
    private String stageFilePath;
    private boolean stageCleanup;

    //Constructors
    public StageOperation(FrameworkExecution frameworkExecution, String stageName, boolean StageCleanup) {
        this.setFrameworkExecution(frameworkExecution);
        this.setStageName(stageName);
        this.setStageCleanup(StageCleanup);

        String stageFolderName = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.tmp") + File.separator + "stage";
        FolderTools.createFolder(stageFolderName);
        this.setStageFileName(this.getStageName() + ".db3");
        this.setStageFilePath(FilenameUtils.normalize(stageFolderName + File.separator + this.getStageFileName()));
        this.setStageConnection(new SqliteDatabaseConnection(this.getStageFilePath()));
    }

    public void doCleanup() {
        if (this.isStageCleanup()) {
            FileTools.delete(this.getStageFilePath());
        }
    }

    //Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }


    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }


    public String getStageName() {
        return stageName;
    }


    public void setStageName(String stageName) {
        this.stageName = stageName;
    }


    public SqliteDatabaseConnection getStageConnection() {
        return stageConnection;
    }


    public void setStageConnection(SqliteDatabaseConnection stageConnection) {
        this.stageConnection = stageConnection;
    }


    public String getStageFileName() {
        return stageFileName;
    }


    public void setStageFileName(String stageFileName) {
        this.stageFileName = stageFileName;
    }


    public String getStageFilePath() {
        return stageFilePath;
    }


    public void setStageFilePath(String stageFilePath) {
        this.stageFilePath = stageFilePath;
    }


    public boolean isStageCleanup() {
        return stageCleanup;
    }


    public void setStageCleanup(boolean stageCleanup) {
        this.stageCleanup = stageCleanup;
    }

}