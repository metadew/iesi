package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;

import java.io.File;

/**
 * This class contains the logic to set the configuration for the metadata
 * repository categories
 *
 * @author peter.billen
 */

public class MetadataRepositoryCategoryConfiguration {

    private String category;
    private String prefix;
    private String definitionFileName;
    private String definitionFolderPath;
    private String definitionFilePath;
    private String objectDefinitionFileName;
    private String objectDefinitionFolderPath;
    private String objectDefinitionFilePath;

    public MetadataRepositoryCategoryConfiguration(String category, FrameworkFolderConfiguration folderConfig) {
        this.setCategory(category.toLowerCase());
        this.setDefinitionFolderPath(folderConfig.getFolderAbsolutePath("metadata.def"));
        this.setObjectDefinitionFolderPath(folderConfig.getFolderAbsolutePath("metadata.def"));
        switch (this.getCategory()) {
            case "connectivity":
                this.setDefinitionFileName("ConnectivityTables.json");
                this.setObjectDefinitionFileName("ConnectivityObjects.json");
                this.setPrefix("CXN_");
                break;
            case "control":
                this.setDefinitionFileName("ControlTables.json");
                this.setObjectDefinitionFileName("ControlObjects.json");
                this.setPrefix("CTL_");
                break;
            case "design":
                this.setDefinitionFileName("DesignTables.json");
                this.setObjectDefinitionFileName("DesignObjects.json");
                this.setPrefix("DES_");
                break;
            case "result":
                this.setDefinitionFileName("ResultTables.json");
                this.setObjectDefinitionFileName("ResultObjects.json");
                this.setPrefix("RES_");
                break;
            case "trace":
                this.setDefinitionFileName("TraceTables.json");
                this.setObjectDefinitionFileName("TraceObjects.json");
                this.setPrefix("TRC_");
                break;
            default:
                this.setDefinitionFileName("");
        }
        this.setDefinitionFilePath(this.getDefinitionFolderPath() + File.separator + this.getDefinitionFileName());
        this.setObjectDefinitionFilePath(this.getObjectDefinitionFolderPath() + File.separator + this.getObjectDefinitionFileName());
    }

    // Getters and setters
    public String getDefinitionFileName() {
        return definitionFileName;
    }

    public void setDefinitionFileName(String definitionFileName) {
        this.definitionFileName = definitionFileName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefinitionFolderPath() {
        return definitionFolderPath;
    }

    public void setDefinitionFolderPath(String definitionFolderPath) {
        this.definitionFolderPath = definitionFolderPath;
    }

    public String getDefinitionFilePath() {
        return definitionFilePath;
    }

    public void setDefinitionFilePath(String definitionFilePath) {
        this.definitionFilePath = definitionFilePath;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getObjectDefinitionFileName() {
        return objectDefinitionFileName;
    }

    public void setObjectDefinitionFileName(String objectDefinitionFileName) {
        this.objectDefinitionFileName = objectDefinitionFileName;
    }

    public String getObjectDefinitionFolderPath() {
        return objectDefinitionFolderPath;
    }

    public void setObjectDefinitionFolderPath(String objectDefinitionFolderPath) {
        this.objectDefinitionFolderPath = objectDefinitionFolderPath;
    }

    public String getObjectDefinitionFilePath() {
        return objectDefinitionFilePath;
    }

    public void setObjectDefinitionFilePath(String objectDefinitionFilePath) {
        this.objectDefinitionFilePath = objectDefinitionFilePath;
    }

}