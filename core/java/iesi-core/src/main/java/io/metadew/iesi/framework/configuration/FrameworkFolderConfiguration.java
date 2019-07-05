package io.metadew.iesi.framework.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.definition.FrameworkFolder;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;

public class FrameworkFolderConfiguration {

    private String solutionHome;

    private HashMap<String, FrameworkFolder> folderMap;

    public FrameworkFolderConfiguration(String solutionHome) {
        this.setSolutionHome(solutionHome);
        this.initalizeValues();
    }

    private void initalizeValues() {
        this.setFolderMap(new HashMap<String, FrameworkFolder>());

        StringBuilder initFilePath = new StringBuilder();
        initFilePath.append(this.getSolutionHome());
        initFilePath.append(File.separator);
        initFilePath.append("sys");
        initFilePath.append(File.separator);
        initFilePath.append("init");
        initFilePath.append(File.separator);
        initFilePath.append("FrameworkFolders.json");

        DataObjectOperation dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(initFilePath.toString());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("frameworkfolder")) {
                FrameworkFolder frameworkFolder = objectMapper.convertValue(dataObject.getData(), FrameworkFolder.class);
                StringBuilder folderPath = new StringBuilder();
                folderPath.append(this.getSolutionHome());
                String subFolderPath = frameworkFolder.getPath().replace("/", File.separator);
                folderPath.append(subFolderPath);
                frameworkFolder.setAbsolutePath(FilenameUtils.normalize(folderPath.toString()));
                //this.getFolderMap().put(frameworkFolder.getName(), FilenameUtils.normalize(folderPath.toString()));
                this.getFolderMap().put(frameworkFolder.getName(), frameworkFolder);
            }
        }
    }

    // Create Getters and Setters
    public String getFolderAbsolutePath(String key) {
        return this.getFolderMap().get(key).getAbsolutePath();
    }

    public String getFolderPath(String key) {
        return this.getFolderMap().get(key).getPath();
    }

    public String getSolutionHome() {
        return solutionHome;
    }

    public void setSolutionHome(String solutionHome) {
        this.solutionHome = solutionHome;
    }

    public HashMap<String, FrameworkFolder> getFolderMap() {
        return folderMap;
    }

    public void setFolderMap(HashMap<String, FrameworkFolder> folderMap) {
        this.folderMap = folderMap;
    }

}