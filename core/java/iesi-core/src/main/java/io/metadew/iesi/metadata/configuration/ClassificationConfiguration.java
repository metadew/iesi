//package io.metadew.iesi.metadata.configuration;
//
//import io.metadew.iesi.connection.tools.SQLTools;
//import io.metadew.iesi.metadata.definition.Classification;
//import io.metadew.iesi.metadata.execution.MetadataControl;
//
//import javax.sql.rowset.CachedRowSet;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//
//public class ClassificationConfiguration {
//
//    private Classification classification;
//
//    // Constructors
//    public ClassificationConfiguration(Classification classification) {
//        this.setClassification(classification);
//    }
//
//    public ClassificationConfiguration() {
//    }
//
//    // Insert
//    public String getInsertStatement(String artefactName, String artefactType) {
//        String sql = "";
//
//        sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Classifications");
//        sql += " (ARTIFACT_ID, CLASSIF_ID, CLASSIF_TYP_NM, CLASSIF_NM, CLASSIF_VAL) ";
//        sql += "VALUES ";
//        sql += "(";
//        sql += "(" + SQLTools.GetLookupIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Artefacts"), "ARTEFACT_ID", "where ARTEFACT_NM = '" + artefactName + "' and ARTEFACT_TYP_NM = '" + artefactType + "')");
//        sql += ",";
//        sql += "("
//                + SQLTools.GetNextIdStatement(
//                MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Classifications"), "CLASSIF_ID")
//                + ")";
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getClassification().getType());
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getClassification().getName());
//        sql += ",";
//        sql += SQLTools.GetStringForSQL(this.getClassification().getValue());
//        sql += ")";
//        sql += ";";
//
//        return sql;
//    }
//
//    public Classification getClassification(long classificationId) {
//        Classification classification = new Classification();
//        CachedRowSet crsClassification = null;
//        String queryClassification = "select ARTIFACT_ID, CLASSIF_ID, CLASSIF_TYP_NM, CLASSIF_NM, CLASSIF_VAL from " + MetadataControl.getInstance().getCatalogMetadataRepository().getTableNameByLabel("Classifications")
//                + " where CLASSIF_ID = " + classificationId;
//        crsClassification = MetadataControl.getInstance().getCatalogMetadataRepository().executeQuery(queryClassification, "reader");
//        try {
//            while (crsClassification.next()) {
//                classification.setId(classificationId);
//                classification.setType(crsClassification.getString("CLASSIF_TYP_NM"));
//                classification.setName(crsClassification.getString("CLASSIF_NM"));
//                classification.setValue(crsClassification.getString("CLASSIF_VAL"));
//            }
//            crsClassification.close();
//        } catch (Exception e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//        }
//        return classification;
//    }
//
//    // Getters and Setters
//    public Classification getClassification() {
//        return classification;
//    }
//
//    public void setClassification(Classification classification) {
//        this.classification = classification;
//    }
//
//}