package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.FeatureAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.FeatureDoesNotExistException;
import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeatureConfiguration extends MetadataConfiguration {

    private Feature feature;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public FeatureConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public FeatureConfiguration(Feature feature, FrameworkInstance frameworkInstance) {
        this.setFeature(feature);
        this.verifyVersionExists();
        this.setFrameworkInstance(frameworkInstance);
    }

    // Abstract method implementations
	@Override
	public List<Feature> getAllObjects() {
		return this.getAllFeatures();
	}

    // Checks
    private void verifyVersionExists() {
        if (this.getFeature().getVersion() == null) {
            this.getFeature().setVersion(new FeatureVersion());
        }
    }

    public boolean exists(String featureName, long versionNumber) {
        return getFeature(featureName, versionNumber).isPresent();
    }

    public boolean exists(String featureName) {
        return getFeatureByName(featureName).isEmpty();
    }

    public boolean exists(Feature feature) {
        return exists(feature.getName(), feature.getVersion().getNumber());
    }

    public List<Feature> getAllFeatures() {
        List<Feature> features = new ArrayList<>();
        String queryFeature = "select FEATURE_ID, FEATURE_NM from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features");
        CachedRowSet crsFeature = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryFeature, "reader");

        try {
            while (crsFeature.next()) {
                features.addAll(getFeatureByName(crsFeature.getString("FEATURE_NM")));
            }
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return features;
    }

    public List<Feature> getFeatureByName(String featureName) {
        List<Feature> features = new ArrayList<>();
        String queryFeature = "select FEATURE_ID from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features") + " where FEATURE_NM = '"
                + featureName + "'";
        CachedRowSet crsFeature = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryFeature, "reader");
        try {
            if (crsFeature.size() == 0) {
                return features;
            } else if (crsFeature.size() > 1) {
                // TODO: log;
            }
            crsFeature.next();
            String queryFeatureVersions = "select FEATURE_VRS_NB from "
                    + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions") + " where FEATURE_ID = "
                    + SQLTools.GetStringForSQL(crsFeature.getString("FEATURE_ID"));
            CachedRowSet crsFeatureVersions = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryFeatureVersions, "reader");
            while (crsFeatureVersions.next()) {
                Optional<Feature> feature = getFeature(featureName, crsFeatureVersions.getLong("FEATURE_VRS_NB"));
                if (feature.isPresent()) {
                    features.add(getFeature(featureName, crsFeatureVersions.getLong("FEATURE_VRS_NB")).get());
                } else {
                    // TODO: log
                }
            }
            crsFeatureVersions.close();
            crsFeature.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return features;
    }

    public void deleteFeature(Feature feature) throws FeatureDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting feature {0}-{1}.", feature.getName(), feature.getVersion().getNumber()), Level.TRACE);
        if (!exists(feature)) {
            throw new FeatureDoesNotExistException(
                    MessageFormat.format("Feature {0}-{1} is not present in the repository so cannot be deleted",
                            feature.getName(), feature.getVersion().getNumber()));
        }

        List<String> deleteQuery = getDeleteStatement(feature);
        this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeBatch(deleteQuery);
    }

    public void deleteFeatureByName(String featureName) throws FeatureDoesNotExistException {
        for (Feature feature : getFeatureByName(featureName)) {
            deleteFeature(feature);
        }
    }

    public void insertFeature(Feature feature) throws FeatureAlreadyExistsException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting feature {0}-{1}.", feature.getName(), feature.getVersion().getNumber()), Level.TRACE);
        if (exists(feature)) {
            throw new FeatureAlreadyExistsException(MessageFormat.format(
                    "Feature {0}-{1} already exists", feature.getName(), feature.getVersion().getNumber()));
        }
        List<String> insertStatement = getInsertStatement(feature);
        this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeBatch(insertStatement);
    }

    public void updateFeature(Feature feature) throws FeatureDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating feature {0}-{1}.", feature.getName(), feature.getVersion().getNumber()), Level.TRACE);
        try {
            deleteFeature(feature);
            insertFeature(feature);
        } catch (FeatureDoesNotExistException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Feature {0}-{1} is not present in the repository so cannot be updated",feature.getName(), feature.getVersion().getNumber()),Level.TRACE);
            throw e;
            // throw new ComponentDoesNotExistException(MessageFormat.format(
            //        "Component {0}-{1} is not present in the repository so cannot be updated", component.getName(),  component.getVersion().getNumber()));

        } catch (FeatureAlreadyExistsException e) {
        	// TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Feature {0}-{1} is not deleted correctly during update. {2}",feature.getName(), feature.getVersion().getNumber(), e.toString()),Level.WARN);
        }
    }

    private List<String> getInsertStatement(Feature feature) {
        List<String> queries = new ArrayList<>();
        FeatureVersionConfiguration featureVersionConfiguration = new FeatureVersionConfiguration(this.getFrameworkInstance());
        FeatureParameterConfiguration featureParameterConfiguration = new FeatureParameterConfiguration(this.getFrameworkInstance());
        ScenarioConfiguration scenarioConfiguration = new ScenarioConfiguration(this.getFrameworkInstance());
        StringBuilder sql = new StringBuilder();

        if (getFeatureByName(feature.getName()).size() == 0) {
            queries.add("INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features") +
                    " (FEATURE_ID, FEATURE_TYP_NM, FEATURE_NM, FEATURE_DSC) VALUES (" +
                    SQLTools.GetStringForSQL(feature.getId()) + "," +
                    SQLTools.GetStringForSQL(feature.getType()) + "," +
                    SQLTools.GetStringForSQL(feature.getName()) + "," +
                    SQLTools.GetStringForSQL(feature.getDescription()) + ");");
        }
        // add version
        queries.add(featureVersionConfiguration.getInsertStatement(feature.getId(), feature.getVersion()));

        // add Parameters
        for (FeatureParameter featureParameter :feature.getParameters()) {
            queries.add(featureParameterConfiguration.getInsertStatement(feature.getId(), feature.getVersion().getNumber(), featureParameter));
        }

        // add scenarios
        for (Scenario scenario : feature.getScenarios()) {
            queries.addAll(scenarioConfiguration.getInsertStatement(feature.getId(), feature.getVersion().getNumber(), scenario));
        }

        return queries;
    }

    private List<String> getDeleteStatement(Feature feature) {

        List<String> queries = new ArrayList<>();
        // delete parameters
        queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("FeatureParameters") +
                " WHERE FEATURE_ID = " + SQLTools.GetStringForSQL(feature.getId()) + " AND FEATURE_VRS_NB = " + SQLTools.GetStringForSQL(feature.getVersion().getNumber()) + ";");


        // delete version

        queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions") +
                " WHERE FEATURE_ID = " + SQLTools.GetStringForSQL(feature.getId()) + " AND FEATURE_VRS_NB = " + SQLTools.GetStringForSQL(feature.getVersion().getNumber()) + ";");

        // delete scenarios
        for (Scenario scenario : feature.getScenarios()) {
            queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Scenarios") +
                    " WHERE SCENARIO_ID = " + SQLTools.GetStringForSQL(scenario.getId()) + ";");
            queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("ScenarioParameters") +
                    " WHERE SCENARIO_ID = " + SQLTools.GetStringForSQL(scenario.getId()) + ";");
        }

        // delete feature info if last version
        String countQuery = "SELECT COUNT(DISTINCT FEATURE_VRS_NB ) AS total_versions FROM "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions")
                + " WHERE FEATURE_ID != " + SQLTools.GetStringForSQL(feature.getId()) + ";";
        CachedRowSet crs = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 1) {
                queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features") +
                        " WHERE FEATURE_ID = " + SQLTools.GetStringForSQL(feature.getId()) + ";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return queries;
    }

    private boolean verifyFeatureConfigurationExists(String featureName) {
        Feature feature = new Feature();
        CachedRowSet crsFeature = null;
        String queryFeature = "select FEATURE_ID, FEATURE_TYP_NM, FEATURE_NM, FEATURE_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features") + " where FEATURE_NM = '"
                + featureName + "'";
        crsFeature = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryFeature, "reader");
        try {
            while (crsFeature.next()) {
                feature.setId(crsFeature.getString("FEATURE_ID"));
                feature.setType(crsFeature.getString("FEATURE_TYP_NM"));
                feature.setName(featureName);
                feature.setDescription(crsFeature.getString("FEATURE_DSC"));
            }
            crsFeature.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (feature.getName() == null || feature.getName().equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }

    // Insert
    public String getInsertStatement() {
        String sql = "";
        this.getFeature().setId(IdentifierTools.getFeatureIdentifier(this.getFeature().getName()));

        if (this.exists()) {
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("ScenarioParameters");
            sql += " WHERE FEATURE_ID = '" + this.getFeature().getId() + "'";
            sql += " AND FEATURE_VRS_NB = " + this.getFeature().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Scenarios");
            sql += " WHERE FEATURE_ID = '" + this.getFeature().getId() + "'";
            sql += " AND FEATURE_VRS_NB = " + this.getFeature().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions");
            sql += " WHERE FEATURE_ID = '" + this.getFeature().getId() + "'";
            sql += " AND FEATURE_VRS_NB = " + this.getFeature().getVersion().getNumber();
            sql += ";";
            sql += "\n";
        }

        if (!this.verifyFeatureConfigurationExists(this.getFeature().getName())) {
            sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features");
            sql += " (FEATURE_ID, FEATURE_TYP_NM, FEATURE_NM, FEATURE_DSC) ";
            sql += "VALUES ";
            sql += "(";
            sql += SQLTools.GetStringForSQL(this.getFeature().getId());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getFeature().getType());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getFeature().getName());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getFeature().getDescription());
            sql += ")";
            sql += ";";
        }

        // add FeatureVersion
        String sqlVersion = this.getVersionInsertStatements();
        if (!sqlVersion.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlVersion;
        }

        // add Scenarios
        String sqlScenarios = this.getScenarioInsertStatements();
        if (!sqlScenarios.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlScenarios;
        }

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements();
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }
        return sql;

    }

    private String getVersionInsertStatements() {
        String result = "";

        if (this.getFeature().getVersion() == null)
            return result;

        FeatureVersionConfiguration featureVersionConfiguration = new FeatureVersionConfiguration(
                this.getFeature().getVersion(), this.getFrameworkInstance());
        result += featureVersionConfiguration.getInsertStatement(this.getFeature().getName());

        return result;
    }

    private String getScenarioInsertStatements() {
        String result = "";
        int counter = 0;

        if (this.getFeature().getScenarios() == null)
            return result;

        for (Scenario scenario : this.getFeature().getScenarios()) {
            counter++;
            ScenarioConfiguration scenarioConfiguration = new ScenarioConfiguration(scenario, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += scenarioConfiguration.getInsertStatement(this.getFeature(), counter);
        }

        return result;
    }

    private String getParameterInsertStatements() {
        String result = "";

        if (this.getFeature().getParameters() == null)
            return result;

        for (FeatureParameter featureParameter : this.getFeature().getParameters()) {
            FeatureParameterConfiguration featureParameterConfiguration = new FeatureParameterConfiguration(
                    this.getFeature().getVersion(), featureParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += featureParameterConfiguration.getInsertStatement(this.getFeature());
        }

        return result;
    }

    private Optional<Long> getLatestVersion(String featureName) {
        // TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Fetching latest version for feature {0}.", featureName), Level.DEBUG);
        String queryFeatureVersion = "select max(FEATURE_VRS_NB) as \"MAX_VRS_NB\" from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions") + " a inner join "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features")
                + " b on a.feature_id = b.feature_id where b.feature_nm = '" + featureName + "'";
        CachedRowSet crsFeatureVersion = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryFeatureVersion, "reader");
        try {
            if (crsFeatureVersion.size() == 0) {
                crsFeatureVersion.close();
                return Optional.empty();
            } else {
                crsFeatureVersion.next();
                long latestFeatureVersion = crsFeatureVersion.getLong("MAX_VRS_NB");
                crsFeatureVersion.close();
                return Optional.of(latestFeatureVersion);
            }
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            //TODO fix logging
            //this.frameworkExecution.getFrameworkLog().log("exception=" + e, Level.INFO);
            //this.frameworkExecution.getFrameworkLog().log("exception.stacktrace=" + StackTrace, Level.DEBUG);

            return Optional.empty();
        }
    }

    public Optional<Feature> getFeature(String featureName) {
        Optional<Long> latestVersion = getLatestVersion(featureName);
        if (latestVersion.isPresent()) {
            return getFeature(featureName, latestVersion.get());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Feature> getFeature(String featureName, long versionNumber) {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Fetching feature {0}-{1}.", featureName, versionNumber), Level.DEBUG);
        String queryFeature = "select FEATURE_ID, FEATURE_TYP_NM, FEATURE_NM, FEATURE_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features") + " where FEATURE_NM = '"
                + featureName + "'";
        CachedRowSet crsFeature = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryFeature, "reader");
        ScenarioConfiguration scenarioConfiguration = new ScenarioConfiguration(this.getFrameworkInstance());
        FeatureVersionConfiguration featureVersionConfiguration = new FeatureVersionConfiguration(
                this.getFrameworkInstance());
        try {
            if (crsFeature.size() == 0) {
                throw new RuntimeException("feature.error.notfound");
            } else if (crsFeature.size() > 1) {
                //TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Found multiple implementations for feature {0}-{1}. Returning first implementation", feature.getName(), feature.getVersion().getNumber()), Level.DEBUG);
            }
            crsFeature.next();
            String featureId = crsFeature.getString("FEATURE_ID");

            // Get the version
            Optional<FeatureVersion> featureVersion = featureVersionConfiguration.getFeatureVersion(featureId, versionNumber);
            if (!featureVersion.isPresent()) {
                //TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Cannot find version {1} for feature {0}.", feature.getName(), feature.getVersion().getNumber()), Level.WARN);
                return Optional.empty();
            }

            // Get the scenarios
            List<Scenario> scenarios = new ArrayList<>();
            String queryScenarios = "select FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_NB from "
                    + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Scenarios")
                    + " where FEATURE_ID = " + SQLTools.GetStringForSQL(featureId) + " and FEATURE_VRS_NB = " + versionNumber
                    + " order by SCENARIO_NB asc ";
            CachedRowSet crsScenarios = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryScenarios, "reader");

            while (crsScenarios.next()) {
                Optional<Scenario> scenario = scenarioConfiguration.getScenario(featureId, featureVersion.get().getNumber(), crsScenarios.getString("SCENARIO_ID"));
                if (scenario.isPresent()) {
                    scenarios.add(scenario.get());
                } else {
                    //TODO fix logging
                	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Cannot retreive scenario {0} for feature {1}-{2}.", crsScenarios.getString("SCENARIO_ID"), featureName, versionNumber), Level.DEBUG);
                }
            }
            crsScenarios.close();

            // Get parameters
            String queryFeatureParameters = "select FEATURE_ID, FEATURE_VRS_NB, FEATURE_PAR_NM, FEATURE_PAR_VAL from "
                    + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("FeatureParameters")
                    + " where FEATURE_ID = " + SQLTools.GetStringForSQL(featureId) + " and FEATURE_VRS_NB = " + versionNumber;
            CachedRowSet crsFeatureParameters = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository()
                    .executeQuery(queryFeatureParameters, "reader");
            List<FeatureParameter> featureParameters = new ArrayList<>();
            while (crsFeatureParameters.next()) {
                featureParameters.add(new FeatureParameter(crsFeatureParameters.getString("FEATURE_PAR_NM"),
                        crsFeatureParameters.getString("FEATURE_PAR_VAL")));
            }
            crsFeatureParameters.close();
            Feature feature = new Feature(featureId, crsFeature.getString("FEATURE_TYP_NM"), featureName, crsFeature.getString("FEATURE_DSC"),
                    featureVersion.get(), featureParameters, scenarios);
            crsFeature.close();
            return Optional.of(feature);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            //TODO fix logging
            //this.frameworkExecution.getFrameworkLog().log("exception=" + e, Level.INFO);
            //this.frameworkExecution.getFrameworkLog().log("exception.stacktrace=" + StackTrace, Level.DEBUG);

            return Optional.empty();
        }
    }


    // Get
    public ListObject getFeatures() {
        List<Feature> featureList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select FEATURE_NM, FEATURE_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Features") + " order by FEATURE_NM ASC";
        crs = this.getFrameworkInstance().getMetadataControl().getCatalogMetadataRepository().executeQuery(query, "reader");
        FeatureConfiguration featureConfiguration = new FeatureConfiguration(this.getFrameworkInstance());
        try {
            String featureName = "";
            while (crs.next()) {
                featureName = crs.getString("FEATURE_NM");
                featureList.add(featureConfiguration.getFeature(featureName).get());
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return new ListObject(
                FrameworkObjectConfiguration.getFrameworkObjectType(new Feature()),
                featureList);
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}