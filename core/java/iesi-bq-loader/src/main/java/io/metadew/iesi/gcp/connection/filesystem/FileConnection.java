package io.metadew.iesi.gcp.connection.filesystem;

public class FileConnection {

    private String filePath;
    private String fileName;
    private String longName;
    private String extension;
    private String attributes;
    private boolean directory;

    // Getters and Setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }


}