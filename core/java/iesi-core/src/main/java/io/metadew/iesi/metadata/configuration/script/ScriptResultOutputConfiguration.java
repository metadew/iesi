package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.script.ScriptResultOutput;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptResultOutputConfiguration {

    private String runId;
    private ScriptResultOutput scriptResultOutput;

    public ScriptResultOutputConfiguration() {}

    // Methods
	public List<ScriptResultOutput> getScriptResultOutputs(String runId, long processId) {
        List<ScriptResultOutput> scriptResultOutputs = new ArrayList<>();
        String queryScriptResultOutputs = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                + " where RUN_ID = '" + runId + "' and PRC_ID = " + processId + " order by LOAD_TMS asc";
        CachedRowSet crsScriptResultOutputs = MetadataControl.getInstance().getResultMetadataRepository().executeQuery(queryScriptResultOutputs, "reader");
        try {
            while (crsScriptResultOutputs.next()) {
            	ScriptResultOutput scriptResultOutput= new ScriptResultOutput();
                scriptResultOutput = new ScriptResultOutput(crsScriptResultOutputs.getString("OUT_NM"),
                        crsScriptResultOutputs.getString("OUT_VAL"));
                scriptResultOutputs.add(scriptResultOutput);
            }
            crsScriptResultOutputs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        
        return scriptResultOutputs;
    }
    
    
    public Optional<ScriptResultOutput> getScriptOutput(String runId, long processId, String scriptResultOutputName) {
        ScriptResultOutput scriptResultOutput = null;
        String queryScriptResultOutput = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                + " where RUN_ID = '" + runId + "' and PRC_ID = " + processId + " and OUT_NM = '" + scriptResultOutputName + "'";
        CachedRowSet crsScriptResultOutput = MetadataControl.getInstance().getResultMetadataRepository().executeQuery(queryScriptResultOutput, "reader");
        try {
            while (crsScriptResultOutput.next()) {
                scriptResultOutput = new ScriptResultOutput(scriptResultOutputName,
                        crsScriptResultOutput.getString("OUT_VAL"));
            }
            crsScriptResultOutput.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.ofNullable(scriptResultOutput);
    }

    // Getters and Setters
    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public ScriptResultOutput getScriptResultOutput() {
        return scriptResultOutput;
    }

    public void setScriptResultOutput(ScriptResultOutput scriptResultOutput) {
        this.scriptResultOutput = scriptResultOutput;
    }

}