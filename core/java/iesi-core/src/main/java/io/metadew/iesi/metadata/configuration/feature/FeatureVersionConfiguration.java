package io.metadew.iesi.metadata.configuration.feature;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.feature.FeatureVersion;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class FeatureVersionConfiguration {

    private FeatureVersion featureVersion;

    // Constructors
    public FeatureVersionConfiguration(FeatureVersion featureVersion) {
        this.setFeatureVersion(featureVersion);
    }

    public FeatureVersionConfiguration() {
    }

    public String getInsertStatement(String featureId, FeatureVersion featureVersion) {
        return "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions") +
                " (FEATURE_ID, FEATURE_VRS_NB, FEATURE_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(featureId) + ", " +
                featureVersion.getNumber() + ", " +
                featureVersion.getDescription() + ");";
    }

    // Insert
    public String getInsertStatement(String featureName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions");
        sql += " (FEATURE_ID, FEATURE_VRS_NB, FEATURE_VRS_DSC) VALUES (";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Features"), "FEATURE_ID", "where FEATURE_NM = '" + featureName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getFeatureVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getFeatureVersion().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }


    public String getDefaultInsertStatement(String featureName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions");
        sql += " (FEATURE_ID, FEATURE_VRS_NB, FEATURE_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Features"), "FEATURE_ID", "where FEATURE_NM = '" + featureName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL("0");
        sql += ",";
        sql += SQLTools.GetStringForSQL("Default version");
        sql += ")";
        sql += ";";

        return sql;
    }

    public Optional<FeatureVersion> getFeatureVersion(String featureId, long featureVersionNumber) {
        String queryFeatureVersion = "select FEATURE_ID, FEATURE_VRS_NB, FEATURE_VRS_DSC from " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("FeatureVersions")
                + " where FEATURE_ID = " + SQLTools.GetStringForSQL(featureId) + " and FEATURE_VRS_NB = " + featureVersionNumber;
        CachedRowSet crsFeatureVersion = MetadataControl.getInstance().getCatalogMetadataRepository().executeQuery(queryFeatureVersion, "reader");
        try {
            if (crsFeatureVersion.size() == 0) {
                return Optional.empty();
            } else if (crsFeatureVersion.size() > 1) {
                //TODO: log
            }
            crsFeatureVersion.next();
            FeatureVersion featureVersion = new FeatureVersion(featureId, featureVersionNumber, crsFeatureVersion.getString("FEATURE_VRS_DSC"));
            crsFeatureVersion.close();
            return Optional.of(featureVersion);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
    }

    // Getters and Setters
    public FeatureVersion getFeatureVersion() {
        return featureVersion;
    }

    public void setFeatureVersion(FeatureVersion featureVersion) {
        this.featureVersion = featureVersion;
    }

}