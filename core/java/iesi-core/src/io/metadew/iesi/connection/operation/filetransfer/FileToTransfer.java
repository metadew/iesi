package io.metadew.iesi.connection.operation.filetransfer;


public class FileToTransfer {

    private String longName;
    private String fileName;
    private String attributes;

    public FileToTransfer(String longName, String fileName, String attributes) {
        this.setLongName(longName);
        this.setFileName(fileName);
        this.setAttributes(attributes);
    }

    // Getters and Setters
    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
