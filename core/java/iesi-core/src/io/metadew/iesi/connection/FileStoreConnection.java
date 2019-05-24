package io.metadew.iesi.connection;

/**
 * Connection object for file stores.
 *
 * @author peter.billen
 */
public class FileStoreConnection {

    private String path;

    public FileStoreConnection() {

    }

    public FileStoreConnection(String path) {
        super();
        this.setPath(path);
    }

    // Methods


    // Getters and Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
