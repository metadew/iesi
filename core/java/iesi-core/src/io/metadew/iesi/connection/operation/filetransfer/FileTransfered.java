package io.metadew.iesi.connection.operation.filetransfer;


public class FileTransfered {

    private String sourceFilePath;
    private String sourceFileName;
    private String targetFilePath;
    private String targetFileName;

    public FileTransfered(String sourceFilePath, String sourceFileName, String targetFilePath, String targetFileName) {
        this.setSourceFilePath(sourceFilePath);
        this.setSourceFileName(sourceFileName);
        this.setTargetFilePath(targetFilePath);
        this.setTargetFileName(targetFileName);
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }


}
