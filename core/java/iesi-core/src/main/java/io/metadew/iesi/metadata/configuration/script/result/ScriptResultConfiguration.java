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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ScriptResultConfiguration extends Configuration<ScriptResult, ScriptResultKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;

    private String fetchByRunIdAndPrcIdQuery() {
        return "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, " +
                "STRT_TMS, END_TMS from " + metadataTablesConfiguration.getMetadataTableNameByLabel("ScriptResults").getName() +
                " where RUN_ID = %s and PRC_ID = %s;";
    }

    private String fetchByRunIdQuery() {
        return "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, " +
                "STRT_TMS, END_TMS from " + metadataTablesConfiguration.getMetadataTableNameByLabel("ScriptResults").getName() +
                " where RUN_ID = %s;";
    }

    private String fetchAllQuery() {
        return "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, " +
                "STRT_TMS, END_TMS from " + metadataTablesConfiguration.getMetadataTableNameByLabel("ScriptResults").getName() + ";";
    }

    private String deleteByRunIdAndPrcId() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("ScriptResults").getName() +
                " WHERE " +
                " RUN_ID = %s AND " +
                " PRC_ID = %s;";
    }

    private String insertQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("ScriptResults").getName() +
                " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, SECURITY_GROUP_NAME, ENV_NM, ST_NM, STRT_TMS, END_TMS) " +
                "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
    }

    private String updateQuery() {
        return "UPDATE " + metadataTablesConfiguration.getMetadataTableNameByLabel("ScriptResults").getName() +
                " SET " +
                "PARENT_PRC_ID = %s, SCRIPT_ID = %s, SCRIPT_NM = %s, SCRIPT_VRS_NB = %s, SECURITY_GROUP_NAME = %s, ENV_NM = %s, ST_NM = %s, STRT_TMS = %s, END_TMS = %s" +
                " WHERE " +
                "RUN_ID = %s AND PRC_ID = %s;";
    }


    public ScriptResultConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getResultMetadataRepository());
    }

    @Override
    public Optional<ScriptResult> get(ScriptResultKey scriptResultKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    String.format(fetchByRunIdAndPrcIdQuery(), SQLTools.getStringForSQL(scriptResultKey.getRunId()), SQLTools.getStringForSQL(scriptResultKey.getProcessId())),
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
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery(), "reader");
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
        getMetadataRepository().executeUpdate(String.format(deleteByRunIdAndPrcId(),
                SQLTools.getStringForSQL(scriptResultKey.getRunId()),
                SQLTools.getStringForSQL(scriptResultKey.getProcessId())
        ));
    }

    @Override
    public void insert(ScriptResult scriptResult) {
        LOGGER.trace(String.format("Inserting ScriptResult %s.", scriptResult));
        getMetadataRepository().executeUpdate(String.format(insertQuery(),
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
        getMetadataRepository().executeUpdate(String.format(updateQuery(),
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

    public List<ScriptResult> getByRunId(String runId) {
        try {
            List<ScriptResult> scriptResults = new ArrayList<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    String.format(fetchByRunIdQuery(), SQLTools.getStringForSQL(runId)),
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
}
