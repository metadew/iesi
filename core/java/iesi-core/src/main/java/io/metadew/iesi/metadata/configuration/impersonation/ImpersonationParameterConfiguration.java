package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ImpersonationParameterConfiguration extends Configuration<ImpersonationParameter, ImpersonationParameterKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ImpersonationParameterConfiguration INSTANCE;

    public synchronized static ImpersonationParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImpersonationParameterConfiguration();
        }
        return INSTANCE;
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ImpersonationParameterConfiguration() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(MetadataRepositoryConfiguration.getInstance()
                .getDesignMetadataRepository()
                .getRepositoryCoordinator()
                .getDatabases().values().stream()
                .findFirst()
                .map(Database::getConnectionPool)
                .orElseThrow(RuntimeException::new));
    }

    private static final String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ImpersonationParameters").getName()
            + " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC)  VALUES (:name, :parameter, :ImpersonatedConnection , :desc );";
    private static final String delete = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ImpersonationParameters").getName() + " WHERE  IMP_NM = :name and CONN_NM =  :conn ;";

    @Override
    public Optional<ImpersonationParameter> get(ImpersonationParameterKey impersonationParameterKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ImpersonationParameter> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ImpersonationParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionResultOutput {0}.", metadataKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", metadataKey.getImpersonationKey().getName())
                .addValue("conn", metadataKey.getParameterName());

        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }

    @Override
    public void insert(ImpersonationParameter metadata) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", metadata.getMetadataKey().getImpersonationKey().getName())
                .addValue("parameter", metadata.getMetadataKey().getParameterName())
                .addValue("ImpersonatedConnection", metadata.getImpersonatedConnection())
                .addValue("desc", metadata.getDescription());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }
}