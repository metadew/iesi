package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.script.ScriptDesignTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.script.ScriptDesignTraceDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptDesignTrace;
import io.metadew.iesi.metadata.definition.script.key.ScriptDesignTraceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptDesignTraceConfiguration extends Configuration<ScriptDesignTrace, ScriptDesignTraceKey> {

	private static final Logger LOGGER = LogManager.getLogger();

	// Constructors
	public ScriptDesignTraceConfiguration(MetadataControl metadataControl) {
		super(metadataControl);
	}


	@Override
	public Optional<ScriptDesignTrace> get(ScriptDesignTraceKey scriptDesignTraceKey) throws SQLException {
		String query = "SELECT SCRIPT_ID, PARENT_PRC_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC FROM " +
				getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
				" WHERE " +
				" RUN_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
				" PRC_ID = "  + SQLTools.GetStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
		CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
		if (cachedRowSet.size() == 0) {
			return Optional.empty();
		} else if (cachedRowSet.size() > 1) {
			LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptDesignTrace {0}. Returning first implementation", scriptDesignTraceKey.toString()));
		}
		cachedRowSet.next();
		return Optional.of(new ScriptDesignTrace(scriptDesignTraceKey,
				cachedRowSet.getString("SCRIPT_ID"),
				cachedRowSet.getLong("PARENT_PRC_ID"),
				cachedRowSet.getString("SCRIPT_TYP_NM"),
				cachedRowSet.getString("SCRIPT_NM"),
				cachedRowSet.getString("SCRIPT_DSC")));
	}

	@Override
	public List<ScriptDesignTrace> getAll() throws SQLException {
		List<ScriptDesignTrace> scriptDesignTraces = new ArrayList<>();
		String query = "SELECT RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC FROM " +
				getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptDesignTraces") + ";";
		CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
		while (cachedRowSet.next()) {
			scriptDesignTraces.add(new ScriptDesignTrace(new ScriptDesignTraceKey(
					cachedRowSet.getString("RUN_ID"),
					cachedRowSet.getLong("PRC_ID")),
					cachedRowSet.getString("SCRIPT_ID"),
					cachedRowSet.getLong("PARENT_PRC_ID"),
					cachedRowSet.getString("SCRIPT_TYP_NM"),
					cachedRowSet.getString("SCRIPT_NM"),
					cachedRowSet.getString("SCRIPT_DSC")));
		}
		return scriptDesignTraces;
	}

	@Override
	public void delete(ScriptDesignTraceKey scriptDesignTraceKey) throws MetadataDoesNotExistException, SQLException {
		LOGGER.trace(MessageFormat.format("Deleting ScriptDesignTrace {0}.", scriptDesignTraceKey.toString()));
		if (!exists(scriptDesignTraceKey)) {
			throw new ScriptDesignTraceDoesNotExistException(MessageFormat.format(
					"ScriptTrace {0} does not exists", scriptDesignTraceKey.toString()));
		}
		String deleteStatement = deleteStatement(scriptDesignTraceKey);
		getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);
	}

	private String deleteStatement(ScriptDesignTraceKey scriptDesignTraceKey) {
		return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
				" WHERE " +
				" RUN_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
				" PRC_ID = "  + SQLTools.GetStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
	}

	@Override
	public void insert(ScriptDesignTrace scriptDesignTrace) throws MetadataAlreadyExistsException, SQLException {
		LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", scriptDesignTrace.toString()));
		if (exists(scriptDesignTrace.getMetadataKey())) {
			throw new ScriptDesignTraceAlreadyExistsException(MessageFormat.format(
					"ActionParameterTrace {0} already exists", scriptDesignTrace.getMetadataKey().toString()));
		}
		String insertStatement = insertStatement(scriptDesignTrace);
		getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
	}

	private String insertStatement(ScriptDesignTrace scriptDesignTrace) {
		return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
				" (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
				SQLTools.GetStringForSQL(scriptDesignTrace.getMetadataKey().getRunId()) + "," +
				SQLTools.GetStringForSQL(scriptDesignTrace.getMetadataKey().getProcessId()) + "," +
				SQLTools.GetStringForSQL(scriptDesignTrace.getParentProcessId()) + "," +
				SQLTools.GetStringForSQL(scriptDesignTrace.getScriptId()) + "," +
				SQLTools.GetStringForSQL(scriptDesignTrace.getScriptType()) + "," +
				SQLTools.GetStringForSQL(scriptDesignTrace.getScriptName()) + "," +
				SQLTools.GetStringForSQL(scriptDesignTrace.getScriptDescription()) + ");";
	}
}