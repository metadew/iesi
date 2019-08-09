package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Feature;
import io.metadew.iesi.metadata.definition.Scenario;
import io.metadew.iesi.metadata.definition.ScenarioParameter;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScenarioConfiguration {

    private Scenario scenario;
    private FrameworkInstance frameworkInstance;

    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public ScenarioConfiguration(Scenario scenario, FrameworkInstance frameworkInstance) {
        this.setScenario(scenario);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScenarioConfiguration(FrameworkInstance frameworkInstance) {
        this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(Feature feature, int scenarioNumber) {
        String sql = "";
        long featureVersionNumber = feature.getVersion().getNumber();
        this.getScenario().setId(IdentifierTools.getScenarioIdentifier(this.getScenario().getName()));

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                .getTableNameByLabel("Scenarios");
        sql += " (FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_NB, SCENARIO_TYP_NM, SCENARIO_NM, SCENARIO_DSC, SCENARIO_DEP, SCRIPT_NM, SCRIPT_VRS_NB, GAIN_VAL) ";
        sql += "VALUES (";
        sql += SQLTools.GetStringForSQL(feature.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(featureVersionNumber);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scenarioNumber);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getDescription());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getDependencies());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getScript());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getVersion());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenario().getGain());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(feature);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(Feature feature) {
        StringBuilder result = new StringBuilder();

        for (ScenarioParameter scenarioParameter : this.getScenario().getParameters()) {
            ScenarioParameterConfiguration scenarioParameterConfiguration = new ScenarioParameterConfiguration(scenarioParameter, this.getFrameworkInstance());
            if (!result.toString().equalsIgnoreCase(""))
                result.append("\n");
            result.append(scenarioParameterConfiguration.getInsertStatement(feature, this.getScenario()));
        }

        return result.toString();
    }


    public Optional<Scenario> getScenario(Feature feature, String scenarioId) {
        return getScenario(feature.getId(), feature.getVersion().getNumber(), scenarioId);
    }

    public Optional<Scenario> getScenario(String featureId, long featureVersionNumber, String scenarioId) {
        LOGGER.trace(MessageFormat.format("Fetching scenario {0}.", scenarioId));
        String queryScenario = "select FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_NB, SCENARIO_TYP_NM, SCENARIO_NM, SCENARIO_DSC, SCENARIO_DEP, SCRIPT_NM, SCRIPT_VRS_NB, GAIN_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                .getTableNameByLabel("Scenarios")
                + " where SCENARIO_ID = " + SQLTools.GetStringForSQL(scenarioId) + " AND FEATURE_ID = " + SQLTools.GetStringForSQL(featureId) + " AND FEATURE_VRS_NB = '" + featureVersionNumber + "'";
        CachedRowSet crsScenario = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryScenario, "reader");
        try {
            if (crsScenario.size() == 0) {
                return Optional.empty();
            } else if (crsScenario.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for scenario {0}. Returning first implementation", scenarioId));
            }
            crsScenario.next();
            // Get parameters
            String queryScenarioParameters = "select SCENARIO_ID, SCENARIO_PAR_NM, SCENARIO_PAR_VAL from "
                    + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("ScenarioParameters")
                    + " where SCENARIO_ID = " + SQLTools.GetStringForSQL(scenarioId) + " AND FEATURE_ID = " + SQLTools.GetStringForSQL(featureId) + " AND FEATURE_VRS_NB = '" + featureVersionNumber + "'";
            CachedRowSet crsScenarioParameters = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryScenarioParameters, "reader");
            List<ScenarioParameter> scenarioParameters = new ArrayList<>();
            while (crsScenarioParameters.next()) {
                scenarioParameters.add(new ScenarioParameter(crsScenarioParameters.getString("SCENARIO_PAR_NM"),
                        crsScenarioParameters.getString("SCENARIO_PAR_VAL")));
            }
            Scenario scenario = new Scenario(scenarioId,
                    crsScenario.getLong("SCENARIO_NB"),
                    crsScenario.getString("SCENARIO_TYP_NM"),
                    crsScenario.getString("SCENARIO_NM"),
                    crsScenario.getString("SCENARIO_DSC"),
                    crsScenario.getString("SCENARIO_DEP"),
                    crsScenario.getString("SCRIPT_NM"),
                    crsScenario.getLong("SCRIPT_VRS_NB"),
                    crsScenario.getLong("GAIN_VAL"),
                    scenarioParameters
            );
            crsScenarioParameters.close();
            crsScenario.close();
            return Optional.of(scenario);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            LOGGER.warn("exception=" + e);
            LOGGER.info("exception.stacktrace=" + StackTrace);

            return Optional.empty();
        }
    }

    public List<String> getInsertStatement(String featureId, long featureVersionNumber, Scenario scenario) {
        ScenarioParameterConfiguration scenarioParameterConfiguration = new ScenarioParameterConfiguration(this.getFrameworkInstance());
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                .getTableNameByLabel("Scenarios") +
                " (FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_NB, SCENARIO_TYP_NM, SCENARIO_NM, SCENARIO_DSC, SCENARIO_DEP, SCRIPT_NM, SCRIPT_VRS_NB, GAIN_VAL) VALUES (" +
                SQLTools.GetStringForSQL(featureId) + "," +
                SQLTools.GetStringForSQL(featureVersionNumber) + "," +
                SQLTools.GetStringForSQL(scenario.getId()) + "," +
                SQLTools.GetStringForSQL(scenario.getNumber()) + "," +
                SQLTools.GetStringForSQL(scenario.getType()) + "," +
                SQLTools.GetStringForSQL(scenario.getName()) + "," +
                SQLTools.GetStringForSQL(scenario.getDescription()) + "," +
                SQLTools.GetStringForSQL(scenario.getDependencies()) + "," +
                SQLTools.GetStringForSQL(scenario.getScript()) + "," +
                SQLTools.GetStringForSQL(scenario.getVersion()) + "," +
                SQLTools.GetStringForSQL(scenario.getGain()) + ");");

        for (ScenarioParameter scenarioParameter : scenario.getParameters()) {
            queries.add(scenarioParameterConfiguration.getInsertStatement(featureId, featureVersionNumber, scenario.getId(), scenarioParameter));
        }
        return queries;
    }

    // Getters and Setters
    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public FrameworkInstance getFrameworkInstance() {
        return frameworkInstance;
    }

    public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
        this.frameworkInstance = frameworkInstance;
    }
}