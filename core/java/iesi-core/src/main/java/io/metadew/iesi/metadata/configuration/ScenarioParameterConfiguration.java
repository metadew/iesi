package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Feature;
import io.metadew.iesi.metadata.definition.Scenario;
import io.metadew.iesi.metadata.definition.ScenarioParameter;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ScenarioParameterConfiguration {

    private ScenarioParameter scenarioParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScenarioParameterConfiguration(ScenarioParameter scenarioParameter, FrameworkInstance frameworkInstance) {
        this.setScenarioParameter(scenarioParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScenarioParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public String getInsertStatement(String featureId, long featureVersionNumber, String scenarioId, ScenarioParameter scenarioParameter) {
        return "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                .getTableNameByLabel("ScenarioParameters") +
                " (FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_PAR_NM, SCENARIO_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(featureId) + "," +
                featureVersionNumber + "," +
                SQLTools.GetStringForSQL(scenarioId) + "," +
                SQLTools.GetStringForSQL(scenarioParameter.getName()) + "," +
                SQLTools.GetStringForSQL(scenarioParameter.getValue()) + ");";
    }

    // Insert
    public String getInsertStatement(Feature feature, Scenario scenario) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                .getTableNameByLabel("ScenarioParameters");
        sql += " (FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_PAR_NM, SCENARIO_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(feature.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(feature.getVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scenario.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenarioParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenarioParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ScenarioParameter getScenarioParameter(Feature feature, String scenarioId, String scenarioParameterName) {
        ScenarioParameter scenarioParameter = new ScenarioParameter();
        CachedRowSet crsScenarioParameter = null;
        String queryScenarioParameter = "select FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_PAR_NM, SCENARIO_PAR_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                .getTableNameByLabel("ScenarioParameters")
                + " where FEATURE_ID = " + SQLTools.GetStringForSQL(feature.getId()) + " and FEATURE_VRS_NB = " + feature.getVersion().getNumber()
                + " AND SCENARIO_ID = " + SQLTools.GetStringForSQL(scenarioId) + "' and SCENARIO_PAR_NM = '" + scenarioParameterName + "'";
        crsScenarioParameter = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                .executeQuery(queryScenarioParameter, "reader");
        try {
            while (crsScenarioParameter.next()) {
                scenarioParameter.setName(scenarioParameterName);
                scenarioParameter.setValue(crsScenarioParameter.getString("SCENARIO_PAR_VAL"));
            }
            crsScenarioParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return scenarioParameter;
    }

    // Getters and Setters
    public ScenarioParameter getScenarioParameter() {
        return scenarioParameter;
    }

    public void setScenarioParameter(ScenarioParameter scenarioParameter) {
        this.scenarioParameter = scenarioParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}