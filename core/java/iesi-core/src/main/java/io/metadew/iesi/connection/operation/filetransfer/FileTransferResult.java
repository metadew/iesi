package io.metadew.iesi.connection.operation.filetransfer;

import java.util.List;

public class FileTransferResult {

    private int returnCode;
    private List<FileTransfered> dcFileTransferedList;

    public FileTransferResult(int returnCode, List<FileTransfered> DCFileTransferedList) {
        this.setReturnCode(returnCode);
        this.setDcFileTransferedList(DCFileTransferedList);
    }

    // Getters and Setters
    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public List<FileTransfered> getDcFileTransferedList() {
        return dcFileTransferedList;
    }

    public void setDcFileTransferedList(List<FileTransfered> dcFileTransferedList) {
        this.dcFileTransferedList = dcFileTransferedList;
    }


}
