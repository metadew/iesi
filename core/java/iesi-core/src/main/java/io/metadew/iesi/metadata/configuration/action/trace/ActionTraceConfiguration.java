package io.metadew.iesi.metadata.configuration.action.trace;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionTraceKey;
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

public class ActionTraceConfiguration extends Configuration<ActionTrace, ActionTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionTraceConfiguration INSTANCE;

    public synchronized static ActionTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionTraceConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ActionTraceConfiguration() {
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

    private final static String query = "select  ActionTraces.RUN_ID  as ActionTraces_RUN_ID , ActionTraces.PRC_ID  as ActionTraces_PRC_ID , ActionTraces.ACTION_ID  as ActionTraces_ACTION_ID , ActionTraces.ACTION_NB  as ActionTraces_ACTION_NB, ActionTraces.ACTION_TYP_NM  as  ActionTraces_ACTION_TYP_NM , ActionTraces.ACTION_NM  as ActionTraces_ACTION_NM , ActionTraces.ACTION_DSC  as ActionTraces_ACTION_DSC, ActionTraces.COMP_NM  as ActionTraces_COMP_NM, ActionTraces.ITERATION_VAL  as ActionTraces_ITERATION_VAL , ActionTraces.CONDITION_VAL  as  ActionTraces_CONDITION_VAL, ActionTraces.RETRIES_VAL  as ActionTraces_RETRIES_VAL , ActionTraces.EXP_ERR_FL  as ActionTraces_EXP_ERR_FL , ActionTraces.STOP_ERR_FL  as ActionTraces_STOP_ERR_FL   " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionTraces").getName() +
            " ActionTraces where ActionTraces.RUN_ID = :id and ActionTraces.PRC_ID = :process and  ActionTraces.ACTION_ID = :action ;";
    private final static String getAll = "select  ActionTraces.RUN_ID  as ActionTraces_RUN_ID , ActionTraces.PRC_ID  as ActionTraces_PRC_ID , ActionTraces.ACTION_ID  as ActionTraces_ACTION_ID , ActionTraces.ACTION_NB  as ActionTraces_ACTION_NB, ActionTraces.ACTION_TYP_NM  as  ActionTraces_ACTION_TYP_NM , ActionTraces.ACTION_NM  as ActionTraces_ACTION_NM , ActionTraces.ACTION_DSC  as ActionTraces_ACTION_DSC, ActionTraces.COMP_NM  as ActionTraces_COMP_NM, ActionTraces.ITERATION_VAL  as ActionTraces_ITERATION_VAL , ActionTraces.CONDITION_VAL  as  ActionTraces_CONDITION_VAL, ActionTraces.RETRIES_VAL  as ActionTraces_RETRIES_VAL , ActionTraces.EXP_ERR_FL  as ActionTraces_EXP_ERR_FL , ActionTraces.STOP_ERR_FL  as ActionTraces_STOP_ERR_FL   " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionTraces").getName() + " ActionTraces  ;";
    private final static String deleteStatement = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionTraces").getName() +
            "  WHERE  RUN_ID = :id and PRC_ID =:process and ACTION_ID =:action ;";
    private final static String insert = "INSERT INTO  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionTraces").getName() +
            " (RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM," +
            " ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES " +
            " ( :id, :process, :actionId, :actionNumber, :actionType, :actionName, :description,  :component, :iteration, :condition, :retries, :error,:errorStop )";
    private final static String updateStatement = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ActionTraces").getName() +
            " SET ACTION_NB = :actionNumber, ACTION_TYP_NM = :actionType ,ACTION_NM = :actionName, ACTION_DSC = :description, " +
            " COMP_NM = :component, ITERATION_VAL = :iteration, CONDITION_VAL = :condition, RETRIES_VAL = :retries, EXP_ERR_FL = :error, STOP_ERR_FL = :errorStop" +
            " WHERE RUN_ID = :id AND PRC_ID = :process AND ACTION_ID= :actionId ;";

    @Override
    public Optional<ActionTrace> get(ActionTraceKey actionTraceKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionTraceKey.getRunId())
                .addValue("process", actionTraceKey.getProcessId())
                .addValue("action", actionTraceKey.getActionId());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        query,
                        sqlParameterSource,
                        new ActionTraceExtractor())));
    }

    @Override
    public List<ActionTrace> getAll() {
        return namedParameterJdbcTemplate.query(
                getAll,
                new ActionTraceExtractor());
    }

    @Override
    public void delete(ActionTraceKey actionTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", actionTraceKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionTraceKey.getRunId())
                .addValue("process", actionTraceKey.getProcessId())
                .addValue("action", actionTraceKey.getActionId());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }

    @Override
    public void insert(ActionTrace actionTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ActionTrace {0}.", actionTrace.getMetadataKey().toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionTrace.getMetadataKey().getRunId())
                .addValue("process", actionTrace.getMetadataKey().getProcessId())
                .addValue("actionId", actionTrace.getMetadataKey().getActionId())
                .addValue("actionNumber", actionTrace.getNumber())
                .addValue("actionType", actionTrace.getType())
                .addValue("actionName", actionTrace.getName())
                .addValue("description", actionTrace.getDescription())
                .addValue("component", actionTrace.getComponent())
                .addValue("iteration", actionTrace.getIteration())
                .addValue("condition", actionTrace.getCondition())
                .addValue("retries", actionTrace.getRetries())
                .addValue("error", actionTrace.getErrorExpected())
                .addValue("errorStop", actionTrace.getErrorStop());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    @Override
    public void update(ActionTrace actionTrace) {
        LOGGER.trace(MessageFormat.format("Updating ActionTrace {0}.", actionTrace.getMetadataKey().toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", actionTrace.getMetadataKey().getRunId())
                .addValue("process", actionTrace.getMetadataKey().getProcessId())
                .addValue("actionId", actionTrace.getMetadataKey().getActionId())
                .addValue("actionNumber", actionTrace.getNumber())
                .addValue("actionType", actionTrace.getType())
                .addValue("actionName", actionTrace.getName())
                .addValue("description", actionTrace.getDescription())
                .addValue("component", actionTrace.getComponent())
                .addValue("iteration", actionTrace.getIteration())
                .addValue("condition", actionTrace.getCondition())
                .addValue("retries", actionTrace.getRetries())
                .addValue("error", actionTrace.getErrorExpected())
                .addValue("errorStop", actionTrace.getErrorStop());
        namedParameterJdbcTemplate.update(
                updateStatement,
                sqlParameterSource);
    }
}