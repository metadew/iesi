package io.metadew.iesi.metadata.configuration.feature;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.feature.Feature;
import io.metadew.iesi.metadata.definition.feature.FeatureParameter;
import io.metadew.iesi.metadata.definition.feature.FeatureVersion;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class FeatureParameterConfiguration {

    private FeatureVersion featureVersion;
    private FeatureParameter featureParameter;

    // Constructors
    public FeatureParameterConfiguration(FeatureVersion featureVersion, FeatureParameter featureParameter) {
        this.setFeatureVersion(featureVersion);
        this.setFeatureParameter(featureParameter);
    }

    public FeatureParameterConfiguration() {
    }

    // Insert
    public String getInsertStatement(String featureId, long featureVersionNumber, FeatureParameter featureParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("FeatureParameters") +
                " (FEATURE_ID, FEATURE_VRS_NB, FEATURE_PAR_NM, FEATURE_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(featureId) + "," +
                featureVersionNumber + "," +
                SQLTools.GetStringForSQL(this.getFeatureParameter().getName()) + "," +
                SQLTools.GetStringForSQL(this.getFeatureParameter().getValue()) + ");";
    }


    public String getInsertStatement(Feature feature) {
        String sql = "";

        sql += "INSERT INTO "
                + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("FeatureParameters");
        sql += " (FEATURE_ID, FEATURE_VRS_NB, FEATURE_PAR_NM, FEATURE_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("Features"), "FEATURE_ID", "where FEATURE_NM = '" + feature.getName()) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getFeatureVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getFeatureParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getFeatureParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public FeatureParameter getFeatureParameter(String featureId, long featureVersionNumber, String featureParameterName) {
        FeatureParameter featureParameter = new FeatureParameter();
        CachedRowSet crsFeatureParameter = null;
        String queryFeatureParameter = "select FEATURE_ID, FEATURE_VRS_NB, FEATURE_PAR_NM, FEATURE_PAR_VAL from " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("FeatureParameters")
                + " where FEATURE_ID = " + SQLTools.GetStringForSQL(featureId) + "' and FEATURE_VRS_NB = " + featureVersionNumber + " and FEATURE_PAR_NM = '" + featureParameterName + "'";
        crsFeatureParameter = MetadataControl.getInstance().getCatalogMetadataRepository().executeQuery(queryFeatureParameter, "reader");
        try {
            while (crsFeatureParameter.next()) {
                featureParameter.setName(featureParameterName);
                featureParameter.setValue(crsFeatureParameter.getString("FEATURE_PAR_VAL"));
            }
            crsFeatureParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return featureParameter;
    }

    // Getters and Setters
    public FeatureParameter getFeatureParameter() {
        return featureParameter;
    }

    public void setFeatureParameter(FeatureParameter featureParameter) {
        this.featureParameter = featureParameter;
    }

    public FeatureVersion getFeatureVersion() {
        return featureVersion;
    }

    public void setFeatureVersion(FeatureVersion featureVersion) {
        this.featureVersion = featureVersion;
    }

}