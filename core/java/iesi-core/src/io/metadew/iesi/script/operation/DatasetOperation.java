package io.metadew.iesi.script.operation;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.common.json.JsonParsed;
import io.metadew.iesi.common.json.JsonParsedItem;
import io.metadew.iesi.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.SqliteDatabaseConnection;
import io.metadew.iesi.framework.execution.FrameworkExecution;

/**
 * Operation to manage the datasets that have been defined in the script
 * 
 * @author peter.billen
 *
 */
public class DatasetOperation
{

	private FrameworkExecution frameworkExecution;

	private DatabaseConnection datasetConnection;

	private DatabaseConnection metadataConnection;

	private String datasetName;

	private String datasetLabels;

	// Constructors
	@SuppressWarnings("unused")
	public DatasetOperation(FrameworkExecution frameworkExecution, String datasetName, String datasetLabels)
	{
		this.setFrameworkExecution(frameworkExecution);
		this.setDatasetName(datasetName);
		this.setDatasetLabels(datasetLabels);

		ObjectMapper objectMapper = new ObjectMapper();
		String datasetFolderName = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("data") + File.separator + "datasets"
					+ File.separator + this.getDatasetName();
		String metadataFileName = datasetFolderName + File.separator + "metadata" + File.separator + "metadata.db3";
		SqliteDatabaseConnection dcSQLiteConnection = new SqliteDatabaseConnection(metadataFileName);
		this.setMetadataConnection(objectMapper.convertValue(dcSQLiteConnection, DatabaseConnection.class));

		// Derive dataset
		String datasetFileName = "";
		CachedRowSet crs = null;

		String query = "select a.DATASET_INV_ID, a.DATASET_FILE_NM from CFG_DATASET_INV a inner join CFG_DATASET_LBL b on a.DATASET_INV_ID = b.DATASET_INV_ID";
		if (!this.getDatasetLabels().trim().equals(""))
		{
			String where = "";
			String[] parts = this.getDatasetLabels().split(",");
			for (int i = 0; i < parts.length; i++)
			{
				String innerpart = parts[i];
				if (where.trim().equals(""))
				{
					where += " where ";
				}
				else
				{
					where += " and ";
				}
				where += "b.DATASET_LBL_VAL = '";
				where += innerpart;
				where += "'";
			}
			query += where;
		}

		crs = this.getMetadataConnection().executeQuery(query);
		try
		{
			while (crs.next())
			{
				datasetFileName = crs.getString("DATASET_FILE_NM");
			}
			crs.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		// New dataset file name
		if (datasetFileName.trim().equals(""))
		{
			datasetFileName = UUID.randomUUID().toString() + ".db3";
			// register in the metadata
			String sql = "insert into CFG_DATASET_INV (DATASET_INV_ID, DATASET_FILE_NM) Values (";
			sql += "";

		}

		datasetFileName = datasetFolderName + File.separator + "data" + File.separator + datasetFileName;
		dcSQLiteConnection = new SqliteDatabaseConnection(datasetFileName);
		this.setDatasetConnection(objectMapper.convertValue(dcSQLiteConnection, DatabaseConnection.class));
	}

	public String getDataItem(String datasetItem)
	{
		CachedRowSet crs = null;
		String query = "";
		if (!datasetItem.trim().equals(""))
		{
			query = "select ";
			String[] parts = datasetItem.split("\\.");
			query += "value";
			query += " from ";
			query += parts[0];
			query += " where key = '";
			query += parts[1];
			query += "'";
		}

		String value = "";
		crs = this.getDatasetConnection().executeQuery(query);
		try
		{
			while (crs.next())
			{
				value = crs.getString("VALUE");
			}
			crs.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return value;
	}

	public void setDataset(String datasetTableName, JsonParsed jsonParsed)
	{
		// Check if table exists
		String queryTableExists = "select name from sqlite_master where name = '" + datasetTableName + "'";
		CachedRowSet crs = null;
		crs = this.getDatasetConnection().executeQuery(queryTableExists);
		String value = "";
		boolean tableExists = false;
		try
		{
			while (crs.next())
			{
				value = crs.getString("NAME");
				if (value.trim().equalsIgnoreCase(datasetTableName))
				{
					tableExists = true;
				}
			}
			crs.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		// Perform necessary initialization actions
		if (tableExists)
		{
			String clean = "delete from " + datasetTableName;
			this.getDatasetConnection().executeUpdate(clean);
		}
		else
		{
			String create = "CREATE TABLE " + datasetTableName + " (key TEXT, value TEXT)";
			this.getDatasetConnection().executeUpdate(create);
		}

		// Store the data
		try
		{
			for (JsonParsedItem jsonParsedItem : jsonParsed.getJsonParsedItemList())
			{
				String query = "";
				query = "insert into " + datasetTableName + " (key, value) values ('";
				query += jsonParsedItem.getPath();
				query += "','";
				query += jsonParsedItem.getValue();
				query += "')";
				this.getDatasetConnection().executeUpdate(query);
			}
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
	}

	public void setDatasetEntry(String datasetTableName, String key, String value)
	{
		// Store the data
		try
		{
			String query = "";
			query = "insert into " + datasetTableName + " (key, value) values ('";
			query += key;
			query += "','";
			query += value;
			query += "')";
			this.getDatasetConnection().executeUpdate(query);
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
	}

	public void resetDataset(String datasetTableName)
	{
		// Check if table exists
		String queryTableExists = "select name from sqlite_master where name = '" + datasetTableName + "'";
		CachedRowSet crs = null;
		crs = this.getDatasetConnection().executeQuery(queryTableExists);
		String value = "";
		boolean tableExists = false;
		try
		{
			while (crs.next())
			{
				value = crs.getString("NAME");
				if (value.trim().equalsIgnoreCase(datasetTableName))
				{
					tableExists = true;
				}
			}
			crs.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		// Perform necessary initialization actions
		if (tableExists)
		{
			String clean = "delete from " + datasetTableName;
			this.getDatasetConnection().executeUpdate(clean);
		}
		else
		{
			String create = "CREATE TABLE " + datasetTableName + " (key TEXT, value TEXT)";
			this.getDatasetConnection().executeUpdate(create);
		}

	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}

	public String getDatasetName()
	{
		return datasetName;
	}

	public void setDatasetName(String datasetName)
	{
		this.datasetName = datasetName;
	}

	public DatabaseConnection getDatasetConnection()
	{
		return datasetConnection;
	}

	public void setDatasetConnection(DatabaseConnection datasetConnection)
	{
		this.datasetConnection = datasetConnection;
	}

	public DatabaseConnection getMetadataConnection()
	{
		return metadataConnection;
	}

	public void setMetadataConnection(DatabaseConnection metadataConnection)
	{
		this.metadataConnection = metadataConnection;
	}

	public String getDatasetLabels()
	{
		return datasetLabels;
	}

	public void setDatasetLabels(String datasetLabels)
	{
		this.datasetLabels = datasetLabels;
	}

}