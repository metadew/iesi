package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptResultConfiguration extends Configuration<ScriptResult, ScriptResultKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptResultConfiguration INSTANCE;

    public synchronized static ScriptResultConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptResultConfiguration();
        }
        return INSTANCE;
    }

    private ScriptResultConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptResult> get(ScriptResultKey scriptResultKey) {
        try {
            String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, " +
                    "STRT_TMS, END_TMS from " + getMetadataRepository().getTableNameByLabel("ScriptResults")
                    + " where RUN_ID = " + SQLTools.GetStringForSQL(scriptResultKey.getRunId()) + " and PRC_ID = " + SQLTools.GetStringForSQL(scriptResultKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptResult {0}. Returning first implementation", scriptResultKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptResult(scriptResultKey,
                    cachedRowSet.getLong("PARENT_PRC_ID"),
                    cachedRowSet.getString("SCRIPT_ID"),
                    cachedRowSet.getString("SCRIPT_NM"),
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("ENV_NM"),
                    ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))
            ));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptResult> getAll() {
        try {
            List<ScriptResult> scriptResults = new ArrayList<>();
            String query = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, " +
                    "STRT_TMS, END_TMS from " + getMetadataRepository().getTableNameByLabel("ScriptResults") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptResults.add(new ScriptResult(new ScriptResultKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getLong("PARENT_PRC_ID"),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getString("SCRIPT_NM"),
                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
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
    public void delete(ScriptResultKey scriptResultKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptResult {0}.", scriptResultKey.toString()));
        String deleteStatement = deleteStatement(scriptResultKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptResultKey scriptResultKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptResults") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptResultKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptResultKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptResult scriptResult) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptResult {0}.", scriptResult.getMetadataKey().toString()));
        String insertStatement = insertStatement(scriptResult);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptResult scriptResult) {
        return "INSERT INTO "
                + getMetadataRepository().getTableNameByLabel("ScriptResults")
                + " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS) VALUES ("
                + SQLTools.GetStringForSQL(scriptResult.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getParentProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getScriptId()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getScriptName()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getScriptVersion()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getEnvironment()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getStatus().value()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getStartTimestamp()) + "," +
                SQLTools.GetStringForSQL(scriptResult.getEndTimestamp()) + ");";
    }

    @Override
    public void update(ScriptResult scriptResult) {
        LOGGER.trace(MessageFormat.format("Updating ScriptResult {0}.", scriptResult.getMetadataKey().toString()));
        String updateStatement = updateStatement(scriptResult);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptResult scriptResult) {
        return "UPDATE "
                + getMetadataRepository().getTableNameByLabel("ScriptResults")
                + " SET " +
                "PARENT_PRC_ID = " + SQLTools.GetStringForSQL(scriptResult.getParentProcessId()) + "," +
                "SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptResult.getScriptId()) + "," +
                "SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptResult.getScriptName()) + "," +
                "SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptResult.getScriptVersion()) + "," +
                "ENV_NM = " + SQLTools.GetStringForSQL(scriptResult.getEnvironment()) + "," +
                "ST_NM = " + SQLTools.GetStringForSQL(scriptResult.getStatus().value()) + "," +
                "STRT_TMS = " + SQLTools.GetStringForSQL(scriptResult.getStartTimestamp()) + "," +
                "END_TMS = " + SQLTools.GetStringForSQL(scriptResult.getEndTimestamp()) +
                " WHERE " +
                "RUN_ID = " + SQLTools.GetStringForSQL(scriptResult.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(scriptResult.getMetadataKey().getProcessId()) + ";";
    }

    public Optional<ScriptResult> getMostRecentScriptResult(String environment, String scriptName, Long scriptVersion) {
        try {

            String query = "SELECT * FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
                    " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) in (" +
                    " SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS)" +
                    " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
                    " where SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptName) + "," +
                    " SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersion) + "," +
                    " ENV_NM = " + SQLTools.GetStringForSQL(environment) +
                    " group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)" + ");";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            }
            cachedRowSet.next();
            return Optional.of(new ScriptResult(
                    new ScriptResultKey(cachedRowSet.getString("RUN_ID"), cachedRowSet.getLong("PRC_ID")),
                    cachedRowSet.getLong("PARENT_PRC_ID"),
                    cachedRowSet.getString("SCRIPT_ID"),
                    cachedRowSet.getString("SCRIPT_NM"),
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("ENV_NM"),
                    ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))
            ));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ScriptResult> getMostRecentScriptResults() {
        try {
            List<ScriptResult> scriptResults = new ArrayList<>();
            String query = "SELECT * FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
                    " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) in (" +
                    " SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS)" +
                    " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
                    " group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)" + ");";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptResults.add(new ScriptResult(new ScriptResultKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getLong("PARENT_PRC_ID"),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getString("SCRIPT_NM"),
                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
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

    public Long getCount(String environment, String scriptName, Long scriptVersion) {
        try {
            String query = "SELECT COUNT(*) as total_executions from " +
                    MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
                    " WHERE " +
                    "SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptName) + "," +
                    "SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersion) + "," +
                    "ENV_NM = " + SQLTools.GetStringForSQL(environment) + ";";
            CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
            if (crs.next()) {
                return Long.parseLong(crs.getString("total_executions"));
            } else {
                return 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ScriptResult> getByRunId(String runId) {
        try {
            List<ScriptResult> scriptResults = new ArrayList<>();
            String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, " +
                    "STRT_TMS, END_TMS from " + getMetadataRepository().getTableNameByLabel("ScriptResults")
                    + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                scriptResults.add(new ScriptResult(new ScriptResultKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getLong("PARENT_PRC_ID"),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getString("SCRIPT_NM"),
                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
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
}
