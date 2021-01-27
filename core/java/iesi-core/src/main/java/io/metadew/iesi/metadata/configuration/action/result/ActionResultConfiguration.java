package io.metadew.iesi.metadata.configuration.action.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.result.ActionResult;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionResultConfiguration extends Configuration<ActionResult, ActionResultKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionResultConfiguration INSTANCE;

    public synchronized static ActionResultConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionResultConfiguration();
        }
        return INSTANCE;
    }

    private ActionResultConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getResultMetadataRepository());
    }

    @Override
    public Optional<ActionResult> get(ActionResultKey actionResultKey) {
        try {
            String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
                    + getMetadataRepository().getTableNameByLabel("ActionResults")
                    + " where RUN_ID = " + SQLTools.getStringForSQL(actionResultKey.getRunId()) + " and PRC_ID = " + SQLTools.getStringForSQL(actionResultKey.getProcessId())
                    + " and ACTION_ID = " + SQLTools.getStringForSQL(actionResultKey.getActionId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionResult {0}. Returning first implementation", actionResultKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionResult(actionResultKey,
                    cachedRowSet.getLong("SCRIPT_PRC_ID"),
                    cachedRowSet.getString("ACTION_NM"),
                    cachedRowSet.getString("ENV_NM"),
                    ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionResult> getAll() {
        try {
            List<ActionResult> scriptResults = new ArrayList<>();
            String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
                    + getMetadataRepository().getTableNameByLabel("ActionResults") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptResults.add(new ActionResult(new ActionResultKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("ACTION_ID")),
                        cachedRowSet.getLong("SCRIPT_PRC_ID"),
                        cachedRowSet.getString("ACTION_NM"),
                        cachedRowSet.getString("ENV_NM"),
                        ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
            }
            return scriptResults;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionResultKey actionResultKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionResult {0}.", actionResultKey.toString()));
        String deleteStatement = deleteStatement(actionResultKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionResultKey actionResultKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionResults") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(actionResultKey.getRunId()) + " AND " +
                " ACTION_ID = " + SQLTools.getStringForSQL(actionResultKey.getActionId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(actionResultKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ActionResult actionResult) {
        LOGGER.trace(MessageFormat.format("Inserting ActionResult {0}.", actionResult.toString()));
        String insertStatement = insertStatement(actionResult);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionResult actionResult) {
        return "INSERT INTO "
                + getMetadataRepository().getTableNameByLabel("ActionResults")
                + " (RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS) VALUES ("
                + SQLTools.getStringForSQL(actionResult.getMetadataKey().getRunId()) + ","
                + SQLTools.getStringForSQL(actionResult.getMetadataKey().getProcessId()) + ","
                + SQLTools.getStringForSQL(actionResult.getScriptProcessId()) + ","
                + SQLTools.getStringForSQL(actionResult.getMetadataKey().getActionId()) + ","
                + SQLTools.getStringForSQL(actionResult.getActionName()) + ","
                + SQLTools.getStringForSQL(actionResult.getEnvironment()) + ","
                + SQLTools.getStringForSQL(actionResult.getStatus().value()) + ","
                + SQLTools.getStringForSQL(actionResult.getStartTimestamp()) + ","
                + SQLTools.getStringForSQL(actionResult.getEndTimestamp()) + ");";
    }

    @Override
    public void update(ActionResult actionResult) {
        LOGGER.trace(MessageFormat.format("Updating ActionResult {0}.", actionResult.toString()));
        String updateStatement = updateStatement(actionResult);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ActionResult actionResult) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ActionResults") +
                " SET SCRIPT_PRC_ID = " + SQLTools.getStringForSQL(actionResult.getScriptProcessId()) + "," +
                "ACTION_ID = " + SQLTools.getStringForSQL(actionResult.getMetadataKey().getActionId()) + "," +
                "ACTION_NM = " + SQLTools.getStringForSQL(actionResult.getActionName()) + "," +
                "ENV_NM = " + SQLTools.getStringForSQL(actionResult.getEnvironment()) + "," +
                "ST_NM = " + SQLTools.getStringForSQL(actionResult.getStatus().value()) + "," +
                "STRT_TMS = " + SQLTools.getStringForSQL(actionResult.getStartTimestamp()) + "," +
                "END_TMS = " + SQLTools.getStringForSQL(actionResult.getEndTimestamp()) +
                "WHERE RUN_ID = " + SQLTools.getStringForSQL(actionResult.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(actionResult.getMetadataKey().getProcessId()) + ";";


    }
}