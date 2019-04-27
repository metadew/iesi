package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Classification;

public class ClassificationConfiguration {

	private Classification classification;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ClassificationConfiguration(Classification classification, FrameworkExecution frameworkExecution) {
		this.setClassification(classification);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ClassificationConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String artefactName, String artefactType) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Classifications");
		sql += " (ARTIFACT_ID, CLASSIF_ID, CLASSIF_TYP_NM, CLASSIF_NM, CLASSIF_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Artefacts"), "ARTEFACT_ID", "where ARTEFACT_NM = '"+ artefactName + "' and ARTEFACT_TYP_NM = '" + artefactType + "')");
		sql += ",";
		sql += "("
				+ SQLTools.GetNextIdStatement(
						this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Classifications"), "CLASSIF_ID")
				+ ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getClassification().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getClassification().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getClassification().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public Classification getClassification(long classificationId) {
		Classification classification = new Classification();
		CachedRowSet crsClassification = null;
		String queryClassification = "select ARTIFACT_ID, CLASSIF_ID, CLASSIF_TYP_NM, CLASSIF_NM, CLASSIF_VAL from " + this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().getTableNameByLabel("Classifications")
				+ " where CLASSIF_ID = " + classificationId;
		crsClassification = this.getFrameworkExecution().getMetadataControl().getCatalogMetadataRepository().executeQuery(queryClassification, "reader");
		try {
			while (crsClassification.next()) {
				classification.setId(classificationId);
				classification.setType(crsClassification.getString("CLASSIF_TYP_NM"));
				classification.setName(crsClassification.getString("CLASSIF_NM"));
				classification.setValue(crsClassification.getString("CLASSIF_VAL"));
			}
			crsClassification.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return classification;
	}

	// Getters and Setters
	public Classification getClassification() {
		return classification;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}


}