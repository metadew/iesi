package io.metadew.iesi.metadata.configuration.script.result;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.result.exception.ScriptResultAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.result.exception.ScriptResultDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
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

	// Constructors
	public ScriptResultConfiguration(){
		super();
	}

	@Override
	public Optional<ScriptResult> get(ScriptResultKey scriptResultKey) throws SQLException {
		String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, " +
				"STRT_TMS, END_TMS from " + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResults")
				+ " where RUN_ID = " + SQLTools.GetStringForSQL(scriptResultKey.getRunId()) + " and PARENT_PRC_ID = " + SQLTools.GetStringForSQL(scriptResultKey.getProcessId()) + ";";
		CachedRowSet cachedRowSet = MetadataControl.getInstance().getResultMetadataRepository().executeQuery(queryScript, "reader");
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
				cachedRowSet.getString("ST_NM"),
				SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
				SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))
				));
	}

	@Override
	public List<ScriptResult> getAll() throws SQLException {
		List<ScriptResult> scriptResults = new ArrayList<>();
		String query = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, " +
				"STRT_TMS, END_TMS from " + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResults") + ";";
		CachedRowSet cachedRowSet = getMetadataControl().getResultMetadataRepository().executeQuery(query, "reader");
		while (cachedRowSet.next()) {
			scriptResults.add(new ScriptResult(new ScriptResultKey(
					cachedRowSet.getString("RUN_ID"),
					cachedRowSet.getLong("PRC_ID")),
					cachedRowSet.getLong("PARENT_PRC_ID"),
					cachedRowSet.getString("SCRIPT_ID"),
					cachedRowSet.getString("SCRIPT_NM"),
					cachedRowSet.getLong("SCRIPT_VRS_NB"),
					cachedRowSet.getString("ENV_NM"),
					cachedRowSet.getString("ST_NM"),
					SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
					SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
		}
		return scriptResults;
	}

	@Override
	public void delete(ScriptResultKey scriptResultKey) throws MetadataDoesNotExistException, SQLException {
		LOGGER.trace(MessageFormat.format("Deleting ScriptResult {0}.", scriptResultKey.toString()));
		if (!exists(scriptResultKey)) {
			throw new ScriptResultDoesNotExistException(MessageFormat.format(
					"ScriptResult {0} does not exists", scriptResultKey.toString()));
		}
		String deleteStatement = deleteStatement(scriptResultKey);
		getMetadataControl().getResultMetadataRepository().executeUpdate(deleteStatement);
	}

	private String deleteStatement(ScriptResultKey scriptResultKey) {
		return "DELETE FROM " + getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ScriptResults") +
				" WHERE " +
				" RUN_ID = " + SQLTools.GetStringForSQL(scriptResultKey.getRunId()) + " AND " +
				" PRC_ID = "  + SQLTools.GetStringForSQL(scriptResultKey.getProcessId()) + ";";
	}

	@Override
	public void insert(ScriptResult scriptResult) throws MetadataAlreadyExistsException, SQLException {
		LOGGER.trace(MessageFormat.format("Inserting ScriptResult {0}.", scriptResult.getMetadataKey().toString()));
		if (exists(scriptResult.getMetadataKey())) {
			throw new ScriptResultAlreadyExistsException(MessageFormat.format(
					"ScriptResult {0} already exists", scriptResult.getMetadataKey().toString()));
		}
		String insertStatement = insertStatement(scriptResult);
		getMetadataControl().getResultMetadataRepository().executeUpdate(insertStatement);
	}

	private String insertStatement(ScriptResult scriptResult) {
		return "INSERT INTO "
				+ MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResults")
				+ " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS) VALUES ("
				+ SQLTools.GetStringForSQL(scriptResult.getMetadataKey().getRunId()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getMetadataKey().getProcessId()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getParentProcessId()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getScriptId()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getScriptName()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getScriptVersion()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getEnvironment()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getStatus()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getStartTimestamp()) + "," +
				SQLTools.GetStringForSQL(scriptResult.getEndTimestamp()) + ");";
	}

//	public ScriptResult getScript(String runId) {
//		ScriptResult scriptResult = new ScriptResult();
//		String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
//				+ MetadataControl.getInstance().getResultMetadataRepository()
//						.getTableNameByLabel("ScriptResults")
//				+ " where RUN_ID = '" + runId + "' and PARENT_PRC_ID = 0";
//		CachedRowSet crsScriptResult = MetadataControl.getInstance().getResultMetadataRepository()
//				.executeQuery(queryScript, "reader");
//		try {
//			while (crsScriptResult.next()) {
//				scriptResult.setFrameworkRunId(runId);
//				Long processId = crsScriptResult.getLong("PRC_ID");
//				scriptResult.setProcessId(processId);
//				scriptResult.setParentProcessId(crsScriptResult.getLong("PARENT_PRC_ID"));
//				scriptResult.setScriptId(crsScriptResult.getString("SCRIPT_ID"));
//				scriptResult.setScriptName(crsScriptResult.getString("SCRIPT_NM"));
//				scriptResult.setScriptVersion(crsScriptResult.getLong("SCRIPT_VRS_NB"));
//				scriptResult.setEnvironment(crsScriptResult.getString("ENV_NM"));
//				scriptResult.setStatus(crsScriptResult.getString("ST_NM"));
//				scriptResult.setStartTimestamp(crsScriptResult.getString("STRT_TMS"));
//				scriptResult.setEndTimestamp(crsScriptResult.getString("END_TMS"));
//				scriptResult.setScripts(this.getChildScripts(runId));
//				scriptResult.setActions(actionResultConfiguration.getActions(runId));
//				scriptResult.setOutputs(scriptResultOutputConfiguration.getScriptResultOutputs(runId, processId));
//			}
//			crsScriptResult.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		if (scriptResult.getScriptName() == null || scriptResult.getScriptName().equalsIgnoreCase("")) {
//			throw new RuntimeException("scriptresult.error.notfound");
//		}
//
//		return scriptResult;
//	}
//
//
//	public List<ScriptResult> getChildScripts(String runId) {
//		List<ScriptResult> scriptResults = new ArrayList<>();
//		String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
//				+ MetadataControl.getInstance().getResultMetadataRepository()
//						.getTableNameByLabel("ScriptResults")
//				+ " where RUN_ID = '" + runId + "' and PARENT_PRC_ID > 0";
//		CachedRowSet crsScriptResult = MetadataControl.getInstance().getResultMetadataRepository()
//				.executeQuery(queryScript, "reader");
//		try {
//			while (crsScriptResult.next()) {
//				ScriptResult scriptResult = new ScriptResult();
//				scriptResult.setFrameworkRunId(runId);
//				Long processId = crsScriptResult.getLong("PRC_ID");
//				scriptResult.setProcessId(processId);
//				scriptResult.setParentProcessId(crsScriptResult.getLong("PARENT_PRC_ID"));
//				scriptResult.setScriptId(crsScriptResult.getString("SCRIPT_ID"));
//				scriptResult.setScriptName(crsScriptResult.getString("SCRIPT_NM"));
//				scriptResult.setScriptVersion(crsScriptResult.getLong("SCRIPT_VRS_NB"));
//				scriptResult.setEnvironment(crsScriptResult.getString("ENV_NM"));
//				scriptResult.setStatus(crsScriptResult.getString("ST_NM"));
//				scriptResult.setStartTimestamp(crsScriptResult.getString("STRT_TMS"));
//				scriptResult.setEndTimestamp(crsScriptResult.getString("END_TMS"));
//				scriptResult.setOutputs(scriptResultOutputConfiguration.getScriptResultOutputs(runId, processId));
//				scriptResult.setActions(actionResultConfiguration.getActions(runId, processId));
//
//				scriptResults.add(scriptResult);
//			}
//			crsScriptResult.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		return scriptResults;
//	}

}