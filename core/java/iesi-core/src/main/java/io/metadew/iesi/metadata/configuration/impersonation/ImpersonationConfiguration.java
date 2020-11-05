package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ImpersonationConfiguration extends Configuration<Impersonation, ImpersonationKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ImpersonationConfiguration INSTANCE;

    public synchronized static ImpersonationConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImpersonationConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ImpersonationConfiguration() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(MetadataRepositoryConfiguration.getInstance()
                .getDesignMetadataRepository()
                .getRepositoryCoordinator()
                .getDatabases().values().stream()
                .findFirst()
                .map(Database::getConnectionPool)
                .orElseThrow(RuntimeException::new));
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    private final static String query = "select Impersonations.IMP_NM AS Impersonations_IMP_NM, Impersonations.IMP_DSC  AS Impersonations_IMP_DSC," +
            " ImpersonationParameters.IMP_NM as ImpersonationParameters_IMP_NM, ImpersonationParameters.CONN_NM as ImpersonationParameters_CONN_NM , ImpersonationParameters.CONN_IMP_NM as ImpersonationParameters_CONN_IMP_NM, ImpersonationParameters.CONN_IMP_DSC as ImpersonationParameters_CONN_IMP_DSC " +
            " FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Impersonations").getName() +
            " Impersonations LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ImpersonationParameters").getName()
            + " ImpersonationParameters on Impersonations.IMP_NM=ImpersonationParameters.IMP_NM " +
            " where Impersonations.IMP_NM = :name ;";
    private final static String getAll = "select Impersonations.IMP_NM AS Impersonations_IMP_NM, Impersonations.IMP_DSC  AS Impersonations_IMP_DSC," +
            " ImpersonationParameters.IMP_NM as ImpersonationParameters_IMP_NM, ImpersonationParameters.CONN_NM as ImpersonationParameters_CONN_NM , ImpersonationParameters.CONN_IMP_NM as ImpersonationParameters_CONN_IMP_NM, ImpersonationParameters.CONN_IMP_DSC as ImpersonationParameters_CONN_IMP_DSC " +
            " FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Impersonations").getName() +
            " Impersonations LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ImpersonationParameters").getName()
            + " ImpersonationParameters on Impersonations.IMP_NM=ImpersonationParameters.IMP_NM " + " ;";
    private static final String deleteImpersonations = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Impersonations").getName() + " WHERE IMP_NM = :name";
    private static final String deleteImpersonationParameters = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ImpersonationParameters").getName() + " WHERE IMP_NM = :name";
    private static final String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Impersonations").getName()
            + " (IMP_NM, IMP_DSC) VALUES (:name, :desc );";
    private static final String deleteAllImpersonations = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Impersonations").getName() + " ;";
    private static final String deleteAllImpersonationParameters = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ImpersonationParameters").getName() + " ;";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Impersonations").getName() +
            " SET IMP_NM = :name, IMP_DSC = :desc " +
            "  WHERE IMP_NM = :name ; ";


    @Override
    public Optional<Impersonation> get(ImpersonationKey metadataKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", metadataKey.getName());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        query,
                        sqlParameterSource,
                        new ImpersonationExtractor())));
    }

    @Override
    public List<Impersonation> getAll() {
        return namedParameterJdbcTemplate.query(getAll, new ImpersonationExtractor());
    }

    @Override
    public void delete(ImpersonationKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting Impersonation {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", metadataKey.getName());
        namedParameterJdbcTemplate.update(
                deleteImpersonations,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteImpersonationParameters,
                sqlParameterSource);
    }

    @Override
    public void insert(Impersonation impersonation) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", impersonation.getMetadataKey().getName())
                .addValue("desc", impersonation.getDescription());
        for (ImpersonationParameter impersonationParameter : impersonation.getParameters()) {
            ImpersonationParameterConfiguration.getInstance().insert(impersonationParameter);
        }
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public boolean exists(Impersonation impersonation) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", impersonation.getMetadataKey().getName());
        List<Impersonation> impersonations = namedParameterJdbcTemplate.query(
                query, sqlParameterSource, new ImpersonationExtractor());
        return impersonations.size() >= 1;
    }

    public void deleteAllImpersonations() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        namedParameterJdbcTemplate.update(
                deleteAllImpersonations,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteAllImpersonationParameters,
                sqlParameterSource);
    }

    public void updateImpersonation(Impersonation impersonation) {

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", impersonation.getMetadataKey().getName())
                .addValue("desc", impersonation.getDescription());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}