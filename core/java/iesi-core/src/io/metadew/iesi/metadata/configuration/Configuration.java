package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.metadata.configuration.exception.*;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.execution.MetadataControl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public abstract class Configuration<T extends Metadata, V extends MetadataKey> {

    // TODO: once metadata control or framework instance become singleton, this class can become an interface
    private MetadataControl metadataControl;


    // TODO: change metadataControl to MetadataRepository
    // TODO: make singleton
    public Configuration(MetadataControl metadataControl) {
        this.metadataControl = metadataControl;
    }

    public abstract Optional<T> get(V metadataKey) throws SQLException;
    public abstract List<T> getAll() throws SQLException;
    public abstract void delete(V metadataKey) throws MetadataDoesNotExistException, SQLException;
    public abstract void insert(T metadata) throws MetadataAlreadyExistsException, SQLException;

    // TODO: investigate casting
    @SuppressWarnings("unchecked")
	public boolean exists(T metadata) throws SQLException {
        return get((V) metadata.getMetadataKey()).isPresent();
    }

    public boolean exists(V key) throws SQLException {
        return get(key).isPresent();
    }
    
    @SuppressWarnings("unchecked")
	public void update(T metadata) throws SQLException, MetadataDoesNotExistException {
        try {
            delete((V) metadata.getMetadataKey());
            insert(metadata);
        } catch (MetadataDoesNotExistException e) {
            throw e;

        } catch (MetadataAlreadyExistsException e) {
        }
    }

    public MetadataControl getMetadataControl() {
        return metadataControl;
    }
}
