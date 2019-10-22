package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.result.exception.ScriptResultOutputAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.result.exception.ScriptResultOutputDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptResultOutputConfiguration extends Configuration<ScriptResultOutput, ScriptResultOutputKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptResultOutputConfiguration INSTANCE;

    public synchronized static ScriptResultOutputConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptResultOutputConfiguration();
        }
        return INSTANCE;
    }

    private ScriptResultOutputConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptResultOutput> get(ScriptResultOutputKey scriptResultOutputKey) {
        try {
            String query = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + getMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                    + " where RUN_ID = " + SQLTools.GetStringForSQL(scriptResultOutputKey.getRunId())
                    + " and OUT_NM = " + SQLTools.GetStringForSQL(scriptResultOutputKey.getOutputName())
                    + " and PRC_ID = " + scriptResultOutputKey.getProcessId() + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptResultOutput {0}. Returning first implementation", scriptResultOutputKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptResultOutput(scriptResultOutputKey,
                    cachedRowSet.getString("SCRIPT_ID"),
                    cachedRowSet.getString("OUT_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptResultOutput> getAll() {
        try {
            List<ScriptResultOutput> scriptResultOutputs = new ArrayList<>();
            String query = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + getMetadataRepository().getTableNameByLabel("ScriptResultOutputs") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptResultOutputs.add(new ScriptResultOutput(new ScriptResultOutputKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("OUT_NM")),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getString("OUT_VAL")));
            }
            return scriptResultOutputs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptResultOutputKey scriptResultOutputKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptResultOutput {0}.", scriptResultOutputKey.toString()));
        if (!exists(scriptResultOutputKey)) {
            throw new ScriptResultOutputDoesNotExistException(MessageFormat.format(
                    "ActionResultOutput {0} does not exists", scriptResultOutputKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptResultOutputKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptResultOutputKey scriptResultOutputKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptResultOutputs") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptResultOutputKey.getRunId()) + " AND " +
                " OUT_NM = " + SQLTools.GetStringForSQL(scriptResultOutputKey.getOutputName()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptResultOutputKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptResultOutput scriptResultOutput) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptResultOutput {0}.", scriptResultOutput.getMetadataKey().toString()));
        if (exists(scriptResultOutput.getMetadataKey())) {
            throw new ScriptResultOutputAlreadyExistsException(MessageFormat.format(
                    "ActionResult {0} already exists", scriptResultOutput.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptResultOutput);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptResultOutput scriptResultOutput) {
        return "INSERT INTO "
                + getMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                + " (RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL) VALUES ("
                + SQLTools.GetStringForSQL(scriptResultOutput.getMetadataKey().getRunId()) + ","
                + SQLTools.GetStringForSQL(scriptResultOutput.getMetadataKey().getProcessId()) + ","
                + SQLTools.GetStringForSQL(scriptResultOutput.getScriptId()) + ","
                + SQLTools.GetStringForSQL(scriptResultOutput.getMetadataKey().getOutputName()) + ","
                + SQLTools.GetStringForSQL(scriptResultOutput.getValue()) + ");";
    }

    public List<ScriptResultOutput> getByRunId(String runId) {
        List<ScriptResultOutput> resultOutputs = new ArrayList<>();
        try {
            String query = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + getMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                    + " where RUN_ID = " + SQLTools.GetStringForSQL(runId) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                resultOutputs.add(new ScriptResultOutput(new ScriptResultOutputKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("OUT_NM")),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getString("OUT_VAL")));
            }
            return resultOutputs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}