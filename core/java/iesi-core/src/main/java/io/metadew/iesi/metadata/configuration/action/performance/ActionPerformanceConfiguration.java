package io.metadew.iesi.metadata.configuration.action.performance;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.performance.ActionPerformance;
import io.metadew.iesi.metadata.definition.action.performance.key.ActionPerformanceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;
import java.util.Optional;

public class ActionPerformanceConfiguration extends Configuration<ActionPerformance, ActionPerformanceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionPerformanceConfiguration INSTANCE;

    public synchronized static ActionPerformanceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionPerformanceConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ActionPerformanceConfiguration() {
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

    private final static String queryAction = "select RUN_ID, PRC_ID, ACTION_ID, CONTEXT_NM, SCOPE_NM, STRT_TMS, END_TMS, DURATION_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultPerformances").getName() +
            " where RUN_ID = :id AND PRC_ID = :procedureId AND SCOPE_NM = :scope ";
    private final static String getAll = "select RUN_ID, PRC_ID, ACTION_ID, CONTEXT_NM, SCOPE_NM, STRT_TMS, END_TMS, DURATION_VAL  " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultPerformances").getName() + " ; ";
    private final static String insert = "insert into  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultPerformances").getName() +
            " (RUN_ID, PRC_ID, ACTION_ID, SCOPE_NM, CONTEXT_NM, STRT_TMS, END_TMS, DURATION_VAL) values (:id, :procedureId, :action, :scope, :context, :start, :end, :duration" + " ) ";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultPerformances").getName() +
            " SET  CONTEXT_NM=:context, STRT_TMS= start,  END_TMS=:end, DURATION_VAL=:duration WHERE RUN_ID = :id  AND PRC_ID = :procedureId ACTION_ID = :action AND SCOPE_NM = :scope ;";
    private final static String delete = "delete from  " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionResultPerformances").getName()
            + " RUN_ID = :id AND PRC_ID = :procedureId AND SCOPE_NM = :scope ; ";

    @Override
    public Optional<ActionPerformance> get(ActionPerformanceKey key) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", key.getRunId())
                .addValue("procedureId", key.getProcedureId())
                .addValue("scope", key.getScope());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryAction,
                        sqlParameterSource,
                        new ActionPerformanceExtractorConfiguration())));
    }

    @Override
    public List<ActionPerformance> getAll() {
        return namedParameterJdbcTemplate.query(
                getAll,
                new ActionPerformanceExtractorConfiguration());
    }

    @Override
    public void delete(ActionPerformanceKey key) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", key.getRunId())
                .addValue("procedureId", key.getProcedureId())
                .addValue("scope", key.getScope());
        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }

    @Override
    public void insert(ActionPerformance actionPerformance) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionPerformance.getMetadataKey().getRunId())
                .addValue("procedureId", actionPerformance.getMetadataKey().getProcedureId())
                .addValue("action", actionPerformance.getActionId())
                .addValue("scope", actionPerformance.getMetadataKey().getScope())
                .addValue("context", actionPerformance.getContext())
                .addValue("start", actionPerformance.getStartTimestamp())
                .addValue("end", actionPerformance.getEndTimestamp())
                .addValue("duration", actionPerformance.getDuration());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    @Override
    public void update(ActionPerformance actionPerformance) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionPerformance.getMetadataKey().getRunId())
                .addValue("procedureId", actionPerformance.getMetadataKey().getProcedureId())
                .addValue("action", actionPerformance.getActionId())
                .addValue("scope", actionPerformance.getMetadataKey().getScope())
                .addValue("context", actionPerformance.getContext())
                .addValue("start", actionPerformance.getStartTimestamp())
                .addValue("end", actionPerformance.getEndTimestamp())
                .addValue("duration", actionPerformance.getDuration());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}
