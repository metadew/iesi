package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Artefact;
import io.metadew.iesi.metadata.definition.Classification;

public class ArtefactConfiguration
{

	private Artefact artefact;

	private FrameworkExecution frameworkExecution;

	// Constructors
	public ArtefactConfiguration(FrameworkExecution frameworkExecution)
	{
		this.setFrameworkExecution(frameworkExecution);
	}

	public ArtefactConfiguration(Artefact artefact, FrameworkExecution frameworkExecution)
	{
		this.setArtefact(artefact);
		this.setFrameworkExecution(frameworkExecution);
	}

	// Delete
	public String getDeleteStatement()
	{
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("Artefacts");
		sql += " WHERE ARTEFACT_NM = " + SQLTools.GetStringForSQL(this.getArtefact().getName());
		sql += " AND ARTEFACT_TYP_NM = ";
		sql += SQLTools.GetStringForSQL(this.getArtefact().getName());
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("Classifications");
		sql += " WHERE ARTEFACT_ID = (";
		sql += "select ARTEFACT_ID FROM " + this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("Artefacts");
		sql += " WHERE ARTEFACT_NM = " + SQLTools.GetStringForSQL(this.getArtefact().getName());
		sql += " AND ARTEFACT_TYP_NM = ";
		sql += SQLTools.GetStringForSQL(this.getArtefact().getName());
		sql += ")";
		sql += ";";
		sql += "\n";

		return sql;

	}

	// Insert
	public String getInsertStatement()
	{
		String sql = "";

		if (this.exists())
		{
			sql += this.getDeleteStatement();
		}

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("Artefacts");
		sql += " (ARTEFACT_ID, ARTEFACT_NM, ARTEFACT_TYP_NM) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetNextIdStatement(this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("Artefacts"), "ARTEFACT_ID") + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getArtefact().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getArtefact().getType());
		sql += ")";
		sql += ";";

		// add classifications
		String sqlClassifications = this.getClassificationInsertStatements(this.getArtefact().getName(),
					this.getArtefact().getType());
		if (!sqlClassifications.equals(""))
		{
			sql += "\n";
			sql += sqlClassifications;
		}

		return sql;
	}

	private String getClassificationInsertStatements(String artefactName, String artefactType)
	{
		String result = "";

		// Catch null parameters
		if (this.getArtefact().getClassifications() == null)
		{
			return result;
		}

		for (Classification classification : this.getArtefact().getClassifications())
		{
			ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration(classification,
						this.getFrameworkExecution());
			if (!result.equals(""))
			{
				result += "\n";
			}
			result += classificationConfiguration.getInsertStatement(artefactName, artefactType);
		}

		return result;
	}

	// GEt Artefact
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Artefact getArtefact(String artefactName, String artefactType)
	{
		Artefact artefact = new Artefact();
		CachedRowSet crsArtefact = null;
		String queryArtefact = "select ARTEFACT_ID, ARTEFACT_NM, ARTEFACT_TYP_NM from "
					+ this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("Artefacts")
					+ " where ARTEFACT_NM = '" + artefactName + "' AND ARTEFACT_TYP_NM = '" + artefactType + "'";
		crsArtefact = this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
					.executeQuery(queryArtefact);
		ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration(this.getFrameworkExecution());
		try
		{
			while (crsArtefact.next())
			{
				artefact.setName(artefactName);
				artefact.setType(artefactType);
				artefact.setId(crsArtefact.getLong("ARTEFACT_ID"));

				// Get classifications
				CachedRowSet crsArtefactClassifications = null;
				String queryArtefactClassifications = "select ARTIFACT_ID, CLASSIF_ID from "
							+ this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
										.getMetadataTableConfiguration().getTableName("Classifications")
							+ " where ARTEFACT_ID = " + artefact.getId();
				crsArtefactClassifications = this.getFrameworkExecution().getMetadataControl().getCatalogRepositoryConfiguration()
							.executeQuery(queryArtefactClassifications);
				List<Classification> artefactClassificationList = new ArrayList();
				while (crsArtefactClassifications.next())
				{
					artefactClassificationList
								.add(classificationConfiguration.getClassification(crsArtefactClassifications.getLong("CLASSIF_ID")));
				}
				artefact.setClassifications(artefactClassificationList);
				crsArtefactClassifications.close();
			}
			crsArtefact.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return artefact;
	}

	// Exists
	public boolean exists()
	{
		return true;
	}

	// Getters and Setters
	public Artefact getArtefact()
	{
		return artefact;
	}

	public void setArtefact(Artefact artefact)
	{
		this.artefact = artefact;
	}

	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}

}