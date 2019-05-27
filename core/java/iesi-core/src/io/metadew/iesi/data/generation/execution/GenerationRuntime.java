package io.metadew.iesi.data.generation.execution;

import java.io.File;


import io.metadew.iesi.client.execution.ProgressBar;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Generation;
import io.metadew.iesi.metadata.definition.GenerationRule;
import io.metadew.iesi.script.execution.ExecutionControl;


public class GenerationRuntime {

	private FrameworkExecution frameworkExecution;
	private SqliteDatabaseConnection temporaryDatabaseConnection;
	private GenerationObjectExecution generationObjectExecution;
	private ExecutionControl executionControl;
	private String fieldListSelect;
	private String tableName;
	private long numberOfGenerationItems;
	private long numberOfGeneratedItems;
	private boolean printProgressBar = false;

	// Constructors
	public GenerationRuntime(FrameworkExecution frameworkExecution, ExecutionControl executionControl) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setGenerationObjectExecution(new GenerationObjectExecution(this.getFrameworkExecution()));
		this.createTemporaryDatabase();
	}

	private void createTemporaryDatabase() {
		// Create work database
		String temporaryDatabaseFolder = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.tmp")
				+ File.separator + this.getExecutionControl().getRunId();
		FolderTools.createFolder(temporaryDatabaseFolder);
		String temporaryDatabaseFile = "genTempDb" + ".db3";
		SqliteDatabaseConnection sqliteDatabaseConnection = new SqliteDatabaseConnection(temporaryDatabaseFolder + File.separator + temporaryDatabaseFile);
		this.setTemporaryDatabaseConnection(sqliteDatabaseConnection);
		
		// Optimize journalling
		//this.getTemporaryDatabaseConnection().executeUpdate("PRAGMA journal_mode=WAL;");
		// Put asynchronouus behaviour
		//this.getTemporaryDatabaseConnection().executeUpdate("PRAGMA synchronous=OFF;");

	}

	public void addGeneration(Generation generation, long numberOfRecords) {
		// Create generation table
		this.setTableName(generation.getName());
		String query = "";

		query += "CREATE TABLE '" + this.getTableName() + "' ('id' INTEGER PRIMARY KEY AUTOINCREMENT";

		// Loop through the generation rules
		String fieldList = "";
		String fieldListFill = "";
		String fieldListSelect = "";
		int i = 0;
		for (GenerationRule generationRule : generation.getRules()) {
			query += ",";
			query += "'v" + generationRule.getField() + "' TEXT";

			if (i > 0) {
				fieldList += ",";
				fieldListFill += ",";
				fieldListSelect += ",";
			}

			fieldList += "'v" + generationRule.getField() + "'";
			fieldListSelect += "v" + generationRule.getField() + " as '" + generationRule.getField() + "'";
			fieldListFill += "''";
			i++;
		}
		query += ");";
		this.setNumberOfGenerationItems(i * numberOfRecords);
		this.setNumberOfGeneratedItems(0);

		this.setFieldListSelect(fieldListSelect);
		this.getTemporaryDatabaseConnection().executeUpdate(query);
		
		// Create index
		this.getTemporaryDatabaseConnection().executeUpdate("create index " + this.getTableName() + "_id_index on " + this.getTableName() +  "(id)");

		// insert id values
		query = "";
		query += "insert into " + this.getTableName();
		query += " (" + fieldList + ") VALUES (";
		query += fieldListFill;
		query += ")";

		for (int currentRecord = 0; currentRecord < numberOfRecords; currentRecord++) {
			this.getTemporaryDatabaseConnection().executeUpdate(query);
		}
		
	}
	
	public void updateProgress() {
		this.setNumberOfGeneratedItems(this.getNumberOfGeneratedItems()+1);

        double num = this.getNumberOfGeneratedItems();
        double denom = this.getNumberOfGenerationItems();
        int progress = (int) ((num / denom) * 100);

        if (this.isPrintProgressBar()) {
            ProgressBar.printProgressBar(progress);
        }
    }

	// Getters and Setters
	public String getTableName() {
		return tableName;
	}

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldListSelect() {
        return fieldListSelect;
    }

    public void setFieldListSelect(String fieldListSelect) {
        this.fieldListSelect = fieldListSelect;
    }

    public long getNumberOfGenerationItems() {
        return numberOfGenerationItems;
    }

    public void setNumberOfGenerationItems(long numberOfGenerationItems) {
        this.numberOfGenerationItems = numberOfGenerationItems;
    }

    public long getNumberOfGeneratedItems() {
        return numberOfGeneratedItems;
    }

    public void setNumberOfGeneratedItems(long numberOfGeneratedItems) {
        this.numberOfGeneratedItems = numberOfGeneratedItems;
    }

    public GenerationObjectExecution getGenerationObjectExecution() {
        return generationObjectExecution;
    }

    public void setGenerationObjectExecution(GenerationObjectExecution generationObjectExecution) {
        this.generationObjectExecution = generationObjectExecution;
    }

    public boolean isPrintProgressBar() {
        return printProgressBar;
    }

    public void setPrintProgressBar(boolean printProgressBar) {
        this.printProgressBar = printProgressBar;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

	public SqliteDatabaseConnection getTemporaryDatabaseConnection() {
		return temporaryDatabaseConnection;
	}

	public void setTemporaryDatabaseConnection(SqliteDatabaseConnection temporaryDatabaseConnection) {
		this.temporaryDatabaseConnection = temporaryDatabaseConnection;
	}

}