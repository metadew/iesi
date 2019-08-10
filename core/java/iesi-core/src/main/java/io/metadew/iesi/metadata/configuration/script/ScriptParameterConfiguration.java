package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.exception.script.ScriptParameterAlreadyExistsException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

public class ScriptParameterConfiguration {

    private static final Logger LOGGER = LogManager.getLogger();

    public ScriptParameterConfiguration() {
    }

    public void insert(String scriptId, long scriptVersionNumber, ScriptParameter scriptParameter) throws ScriptParameterAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptParameter {0}-{1}.", scriptId, scriptVersionNumber));
        if (exists(scriptId, scriptVersionNumber, scriptParameter)) {
            throw new ScriptParameterAlreadyExistsException(MessageFormat.format(
                    "ScriptParameter {0}-{1} already exists", scriptId, scriptVersionNumber));
        }
        MetadataControl.getInstance().getDesignMetadataRepository().executeUpdate(getInsertStatement(scriptId, scriptVersionNumber, scriptParameter));


    }

    private boolean exists(String scriptId, long scriptVersionNumber, ScriptParameter scriptParameter) {
        String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + " and SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameter.getName()) + ";";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScriptParameter, "reader");
        return cachedRowSet.size() >= 1;
    }

    // Insert
    public String getInsertStatement(String scriptId, long scriptVersionNumber, ScriptParameter scriptParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("ScriptParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                scriptVersionNumber + "," +
                SQLTools.GetStringForSQL(scriptParameter.getName()) + "," +
                SQLTools.GetStringForSQL(scriptParameter.getValue()) + ");";
    }


    public Optional<ScriptParameter> getScriptParameter(String scriptId, long scriptVersionNumber, String scriptParameterName) throws SQLException {
        String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + " and SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameterName) + ";";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScriptParameter, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.info(MessageFormat.format("Found multiple implementations for ScriptParameter {0}-{1}-{2}. Returning first implementation", scriptId, scriptVersionNumber, scriptParameterName));
        }
        cachedRowSet.next();
        return Optional.of(new ScriptParameter(scriptParameterName, cachedRowSet.getString("ACTION_PAR_VAL")));
    }

}