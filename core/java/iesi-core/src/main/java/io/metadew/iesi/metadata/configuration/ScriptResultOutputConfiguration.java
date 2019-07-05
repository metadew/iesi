package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ScriptResultOutput;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptResultOutputConfiguration {

    private String runId;
    private ScriptResultOutput scriptResultOutput;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScriptResultOutputConfiguration(String runId, ScriptResultOutput scriptResultOutput, FrameworkInstance frameworkInstance) {
        this.setRunId(runId);
        this.setScriptResultOutput(scriptResultOutput);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScriptResultOutputConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Methods
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ScriptResultOutput> getScriptResultOutputs(String runId, long processId) {
        List<ScriptResultOutput> scriptResultOutputs = new ArrayList();
        CachedRowSet crsScriptResultOutputs;
        String queryScriptResultOutputs = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                + " where RUN_ID = '" + runId + "' and PRC_ID = " + processId + " order by LOAD_TMS asc";
        crsScriptResultOutputs = this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository().executeQuery(queryScriptResultOutputs, "reader");
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
        CachedRowSet crsScriptResultOutput;
        String queryScriptResultOutput = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                + " where RUN_ID = '" + runId + "' and PRC_ID = " + processId + " and OUT_NM = '" + scriptResultOutputName + "'";
        crsScriptResultOutput = this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository().executeQuery(queryScriptResultOutput, "reader");
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}