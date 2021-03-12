package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
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
    private static ScriptResultConfiguration isntance;

    private static final String FETCH_BY_RUN_ID_AND_PRC_ID_QUERY = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, " +
            "STRT_TMS, END_TMS from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
            " where RUN_ID = %s and PRC_ID = %s;";

    private static final String FETCH_BY_RUN_ID_QUERY = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, " +
            "STRT_TMS, END_TMS from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
            " where RUN_ID = %s;";

    private static final String FETCH_ALL_QUERY = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, " +
            "STRT_TMS, END_TMS from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() + ";";

    private static final String DELETE_BY_RUN_ID_AND_PRC_ID = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
            " WHERE " +
            " RUN_ID = %s AND " +
            " PRC_ID = %s;";

    private static final String INSERT_QUERY = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
            " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, STRT_TMS, END_TMS) " +
            "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";

    private static final String UPDATE_QUERY = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults").getName() +
            " SET " +
            "PARENT_PRC_ID = %s, SCRIPT_ID = %s, SCRIPT_NM = %s, SCRIPT_VRS_NB = %s, SECURITY_GROUP_NAME = %s, ENV_NM = %s, ST_NM = %s, STRT_TMS = %s, END_TMS = %s" +
            " WHERE " +
            "RUN_ID = %s AND PRC_ID = %s;";

    public static synchronized ScriptResultConfiguration getInstance() {
        if (isntance == null) {
            isntance = new ScriptResultConfiguration();
        }
        return isntance;
    }

    private ScriptResultConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getResultMetadataRepository());
    }

    @Override
    public Optional<ScriptResult> get(ScriptResultKey scriptResultKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    String.format(FETCH_BY_RUN_ID_AND_PRC_ID_QUERY, SQLTools.getStringForSQL(scriptResultKey.getRunId()), SQLTools.getStringForSQL(scriptResultKey.getProcessId())),
                    "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptResult {0}. Returning first implementation", scriptResultKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(mapRow(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptResult> getAll() {
        try {
            List<ScriptResult> scriptResults = new ArrayList<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(FETCH_ALL_QUERY, "reader");
            while (cachedRowSet.next()) {
                scriptResults.add(mapRow(cachedRowSet));
            }
            return scriptResults;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptResultKey scriptResultKey) {
        LOGGER.trace(String.format("Deleting ScriptResult %s.", scriptResultKey));
        getMetadataRepository().executeUpdate(String.format(DELETE_BY_RUN_ID_AND_PRC_ID,
                SQLTools.getStringForSQL(scriptResultKey.getRunId()),
                SQLTools.getStringForSQL(scriptResultKey.getProcessId())
        ));
    }

    @Override
    public void insert(ScriptResult scriptResult) {
        LOGGER.trace(String.format("Inserting ScriptResult %s.", scriptResult));
        getMetadataRepository().executeUpdate(String.format(INSERT_QUERY,
                SQLTools.getStringForSQL(scriptResult.getMetadataKey().getRunId()),
                SQLTools.getStringForSQL(scriptResult.getMetadataKey().getProcessId()),
                SQLTools.getStringForSQL(scriptResult.getParentProcessId()),
                SQLTools.getStringForSQL(scriptResult.getScriptId()),
                SQLTools.getStringForSQL(scriptResult.getScriptName()),
                SQLTools.getStringForSQL(scriptResult.getScriptVersion()),
                SQLTools.getStringForSQL(scriptResult.getSecurityGroupName()),
                SQLTools.getStringForSQL(scriptResult.getEnvironment()),
                SQLTools.getStringForSQL(scriptResult.getStatus().value()),
                SQLTools.getStringForSQL(scriptResult.getStartTimestamp()),
                SQLTools.getStringForSQL(scriptResult.getEndTimestamp())
        ));
    }

    @Override
    public void update(ScriptResult scriptResult) {
        LOGGER.trace(MessageFormat.format("Updating ScriptResult {0}.", scriptResult.getMetadataKey().toString()));
        getMetadataRepository().executeUpdate(String.format(UPDATE_QUERY,
                SQLTools.getStringForSQL(scriptResult.getParentProcessId()),
                SQLTools.getStringForSQL(scriptResult.getScriptId()),
                SQLTools.getStringForSQL(scriptResult.getScriptName()),
                SQLTools.getStringForSQL(scriptResult.getScriptVersion()),
                SQLTools.getStringForSQL(scriptResult.getSecurityGroupName()),
                SQLTools.getStringForSQL(scriptResult.getEnvironment()),
                SQLTools.getStringForSQL(scriptResult.getStatus().value()),
                SQLTools.getStringForSQL(scriptResult.getStartTimestamp()),
                SQLTools.getStringForSQL(scriptResult.getEndTimestamp()),
                SQLTools.getStringForSQL(scriptResult.getMetadataKey().getRunId()),
                SQLTools.getStringForSQL(scriptResult.getMetadataKey().getProcessId())
        ));
    }

//    public Optional<ScriptResult> getMostRecentScriptResult(String environment, String scriptName, Long scriptVersion) {
//        try {
//
//            String query = "SELECT * FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
//                    " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) in (" +
//                    " SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS)" +
//                    " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
//                    " where SCRIPT_NM = " + SQLTools.getStringForSQL(scriptName) + "," +
//                    " SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersion) + "," +
//                    " ENV_NM = " + SQLTools.getStringForSQL(environment) +
//                    " group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)" + ");";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
//            if (cachedRowSet.size() == 0) {
//                return Optional.empty();
//            }
//            cachedRowSet.next();
//            return Optional.of(new ScriptResult(
//                    new ScriptResultKey(cachedRowSet.getString("RUN_ID"), cachedRowSet.getLong("PRC_ID")),
//                    cachedRowSet.getLong("PARENT_PRC_ID"),
//                    cachedRowSet.getString("SCRIPT_ID"),
//                    cachedRowSet.getString("SCRIPT_NM"),
//                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
//                    securityGroupName, cachedRowSet.getString("ENV_NM"),
//                    ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
//                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
//                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))
//            ));
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public List<ScriptResult> getMostRecentScriptResults() {
//        try {
//            List<ScriptResult> scriptResults = new ArrayList<>();
//            String query = "SELECT * FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
//                    " where (SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, STRT_TMS) in (" +
//                    " SELECT SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, MAX(STRT_TMS)" +
//                    " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
//                    " group by SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM)" + ");";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
//            while (cachedRowSet.next()) {
//                scriptResults.add(new ScriptResult(new ScriptResultKey(
//                        cachedRowSet.getString("RUN_ID"),
//                        cachedRowSet.getLong("PRC_ID")),
//                        cachedRowSet.getLong("PARENT_PRC_ID"),
//                        cachedRowSet.getString("SCRIPT_ID"),
//                        cachedRowSet.getString("SCRIPT_NM"),
//                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
//                        securityGroupName, cachedRowSet.getString("ENV_NM"),
//                        ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
//                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
//                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
//            }
//            return scriptResults;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Long getCount(String environment, String scriptName, Long scriptVersion) {
//        try {
//            String query = "SELECT COUNT(*) as total_executions from " +
//                    MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptResults") +
//                    " WHERE " +
//                    "SCRIPT_NM = " + SQLTools.getStringForSQL(scriptName) + "," +
//                    "SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersion) + "," +
//                    "ENV_NM = " + SQLTools.getStringForSQL(environment) + ";";
//            CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
//            if (crs.next()) {
//                return Long.parseLong(crs.getString("total_executions"));
//            } else {
//                return 0L;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public List<ScriptResult> getByRunId(String runId) {
        try {
            List<ScriptResult> scriptResults = new ArrayList<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    String.format(FETCH_BY_RUN_ID_QUERY, SQLTools.getStringForSQL(runId)),
                    "reader");
            while (cachedRowSet.next()) {
                scriptResults.add(mapRow(cachedRowSet));
            }
            return scriptResults;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ScriptResult mapRow(CachedRowSet cachedRowSet) throws SQLException {
        // security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
        //            "security_group_teams.team_id as security_group_teams_team_id, " +
        //            "teams.id as team_id, teams.TEAM_NAME as team_name " +
        return new ScriptResult(
                new ScriptResultKey(cachedRowSet.getString("RUN_ID"), cachedRowSet.getLong("PRC_ID")),
                cachedRowSet.getLong("PARENT_PRC_ID"),
                cachedRowSet.getString("SCRIPT_ID"),
                cachedRowSet.getString("SCRIPT_NM"),
                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                cachedRowSet.getString("SECURITY_GROUP_NAME"),
                cachedRowSet.getString("ENV_NM"),
                ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS")));
    }
<<<<<<< HEAD

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
=======
>>>>>>> master
}
