package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public abstract class Configuration<T extends Metadata, V extends MetadataKey> {

    // TODO: once metadata control or framework instance become singleton, this class can become an interface
    private static final Logger LOGGER = LogManager.getLogger();
    private MetadataRepository metadataRepository;


    // TODO: change metadataControl to MetadataRepository
    // TODO: make singleton
    public Configuration() {
    }
//hello
    public abstract Optional<T> get(V metadataKey);

    public abstract List<T> getAll() throws SQLException;

    public abstract void delete(V metadataKey);

    public abstract void insert(T metadata);

    @SuppressWarnings("unchecked")
    public boolean exists(T metadata) {
        return exists((V) metadata.getMetadataKey());
    }

    public boolean exists(V key) {
        return get(key).isPresent();
    }

    @SuppressWarnings("unchecked")
    public void update(T metadata) {
        delete((V) metadata.getMetadataKey());
        insert(metadata);
    }

    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }
}
