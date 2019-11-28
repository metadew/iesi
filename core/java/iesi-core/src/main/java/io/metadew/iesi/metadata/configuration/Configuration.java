package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public abstract class Configuration<T extends Metadata, V extends MetadataKey> {

    // TODO: once metadata control or framework instance become singleton, this class can become an interface
    private static final Logger LOGGER = LogManager.getLogger();
    private MetadataRepository metadataRepository;


    // TODO: change metadataControl to MetadataRepository
    // TODO: make singleton
    public Configuration() {}

    public abstract Optional<T> get(V metadataKey);
    public abstract List<T> getAll();
    public abstract void delete(V metadataKey) throws MetadataDoesNotExistException;
    public abstract void insert(T metadata) throws MetadataAlreadyExistsException;

    @SuppressWarnings("unchecked")
	public boolean exists(T metadata) {
        return get((V) metadata.getMetadataKey()).isPresent();
    }

    public boolean exists(V key) {
        return get(key).isPresent();
    }

    @SuppressWarnings("unchecked")
	public void update(T metadata) throws MetadataDoesNotExistException {
        try {
            delete((V) metadata.getMetadataKey());
            insert(metadata);
        } catch (MetadataDoesNotExistException e) {
            throw e;
        } catch (MetadataAlreadyExistsException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace);
        }
    }

    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }
}
