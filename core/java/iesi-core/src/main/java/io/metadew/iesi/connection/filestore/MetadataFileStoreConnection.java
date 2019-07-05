package io.metadew.iesi.connection.filestore;

import io.metadew.iesi.connection.FileStoreConnection;

/**
 * Connection object for a configuration file store. This class extends the file store connection.
 *
 * @author peter.billen
 */
public class MetadataFileStoreConnection extends FileStoreConnection {

    public MetadataFileStoreConnection(String path) {
        super(path);
    }

}
