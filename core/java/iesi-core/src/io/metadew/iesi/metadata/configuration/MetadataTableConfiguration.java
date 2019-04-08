package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata_repository.MetadataRepository;

public class MetadataTableConfiguration {
//
//	private MetadataTable table;
//	private MetadataRepository metadataRepository;
//
//	// Constructors
//	public MetadataTableConfiguration(MetadataTable table, MetadataRepository metadataRepositoryConfiguration) {
//		this.setTable(table);
//		this.setMetadataRepository(metadataRepositoryConfiguration);
//	}
//
//	// Create
//	public String getCreateStatement() {
//		if (this.getMetadataRepository().getType().equalsIgnoreCase("oracle")) {
//			return this.getCreateStatementOracle();
//		} else if (this.getMetadataRepository().getType().equalsIgnoreCase("sqlite")) {
//			return this.getCreateStatementSQLite();
//		} else if (this.getMetadataRepository().getType().equalsIgnoreCase("netezza")) {
//			return this.getCreateStatementNetezza();
//		} else if (this.getMetadataRepository().getType().equalsIgnoreCase("postgresql")) {
//			return this.getCreateStatementPostgresql();
//		} else {
//			return "";
//		}
//	}
//
//	private String getCreateStatementOracle() {
//		String sql = "";
//		String fieldComments = "";
//		String tempTableName = this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix() + this.getTable().getName();
//
//		sql += "CREATE TABLE " + this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "."
//				+ tempTableName;
//		sql += "\n";
//		sql += "(";
//		sql += "\n";
//
//		int counter = 1;
//		for (MetadataField field : this.getTable().getFields()) {
//			if (counter > 1) {
//				sql += ",";
//				sql += "\n";
//			}
//
//			sql += "\t";
//			sql += field.getName();
//
//			int tabNumber = 1;
//			if (field.getName().length() >= 8) {
//				tabNumber = (int) (4 - Math.ceil(field.getName().length() / 8));
//			} else {
//				tabNumber = 4;
//			}
//
//			for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
//				sql += "\t";
//			}
//
//			// Data Types
//			if (field.getType().equals("string")) {
//				sql += "VARCHAR2";
//				sql += " (";
//				sql += field.getLength();
//				sql += " CHAR";
//				sql += ")";
//			} else if (field.getType().equals("flag")) {
//				sql += "CHAR";
//				sql += " (";
//				sql += field.getLength();
//				sql += " CHAR";
//				sql += ")";
//			} else if (field.getType().equals("number")) {
//				sql += "NUMBER";
//			} else if (field.getType().equals("timestamp")) {
//				sql += "TIMESTAMP (6)";
//			}
//
//			// Default DtTimestamp
//			if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
//				sql += " DEFAULT systimestamp";
//			}
//
//			// Nullable
//			if (field.getNullable().trim().equalsIgnoreCase("n")) {
//				sql += " NOT NULL";
//			}
//
//			// Add comments
//			if (!field.getDescription().trim().equals("")) {
//				fieldComments += "\n";
//				fieldComments += "COMMENT ON COLUMN ";
//				fieldComments += this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "."
//						+ tempTableName + "." + field.getName();
//				fieldComments += " IS ";
//				fieldComments += "'";
//				fieldComments += field.getDescription();
//				fieldComments += "'";
//				fieldComments += ";";
//			}
//
//			counter++;
//		}
//		sql += "\n";
//		sql += ")";
//		sql += "\n";
//
//		sql += "LOGGING";
//		sql += "\n";
//		sql += "NOCOMPRESS";
//		sql += "\n";
//		sql += "NOCACHE";
//		sql += "\n";
//		sql += "NOPARALLEL";
//		sql += "\n";
//		sql += "MONITORING;";
//		sql += "\n";
//
//		sql += fieldComments;
//		sql += "\n";
//		sql += "\n";
//
//		return sql;
//	}
//
//	private String getCreateStatementNetezza() {
//		String sql = "";
//		String fieldComments = "";
//		String tempTableName = this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix() + this.getTable().getName();
//
//		sql += "CREATE TABLE " + this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "."
//				+ tempTableName;
//		sql += "\n";
//		sql += "(";
//		sql += "\n";
//
//		int counter = 1;
//		for (MetadataField field : this.getTable().getFields()) {
//			if (counter > 1) {
//				sql += ",";
//				sql += "\n";
//			}
//
//			sql += "\t";
//			sql += field.getName();
//
//			int tabNumber = 1;
//			if (field.getName().length() >= 8) {
//				tabNumber = (int) (4 - Math.ceil(field.getName().length() / 8));
//			} else {
//				tabNumber = 4;
//			}
//
//			for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
//				sql += "\t";
//			}
//
//			// Data Types
//			if (field.getType().equals("string")) {
//				sql += "VARCHAR";
//				sql += " (";
//				sql += field.getLength();
//				sql += ")";
//			} else if (field.getType().equals("flag")) {
//				sql += "CHAR";
//				sql += " (";
//				sql += field.getLength();
//				sql += ")";
//			} else if (field.getType().equals("number")) {
//				sql += "NUMERIC";
//			} else if (field.getType().equals("timestamp")) {
//				sql += "TIMESTAMP";
//			}
//
//			// Default DtTimestamp
//			if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
//				sql += " DEFAULT CURRENT_TIMESTAMP";
//			}
//
//			// Nullable
//			if (field.getNullable().trim().equalsIgnoreCase("n")) {
//				sql += " NOT NULL";
//			}
//
//			// Add comments
//			if (!field.getDescription().trim().equals("")) {
//				fieldComments += "\n";
//				fieldComments += "COMMENT ON COLUMN ";
//				fieldComments += this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "."
//						+ tempTableName + "." + field.getName();
//				fieldComments += " IS ";
//				fieldComments += "'";
//				fieldComments += field.getDescription();
//				fieldComments += "'";
//				fieldComments += ";";
//			}
//
//			counter++;
//		}
//		sql += "\n";
//		sql += ")";
//		sql += "\n";
//
//		sql += ";";
//		sql += "\n";
//
//		sql += fieldComments;
//		sql += "\n";
//		sql += "\n";
//
//		// Grants
//
//		return sql;
//	}
//
//	private String getCreateStatementPostgresql() {
//		String sql = "";
//		String fieldComments = "";
//		String tempTableName = this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix() + this.getTable().getName();
//
//		sql += "CREATE TABLE " + this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "."
//				+ tempTableName;
//		sql += "\n";
//		sql += "(";
//		sql += "\n";
//
//		int counter = 1;
//		for (MetadataField field : this.getTable().getFields()) {
//			if (counter > 1) {
//				sql += ",";
//				sql += "\n";
//			}
//
//			sql += "\t";
//			sql += field.getName();
//
//			int tabNumber = 1;
//			if (field.getName().length() >= 8) {
//				tabNumber = (int) (4 - Math.ceil(field.getName().length() / 8));
//			} else {
//				tabNumber = 4;
//			}
//
//			for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
//				sql += "\t";
//			}
//
//			// Data Types
//			if (field.getType().equals("string")) {
//				sql += "VARCHAR";
//				sql += " (";
//				sql += field.getLength();
//				sql += ")";
//			} else if (field.getType().equals("flag")) {
//				sql += "CHAR";
//				sql += " (";
//				sql += field.getLength();
//				sql += ")";
//			} else if (field.getType().equals("number")) {
//				sql += "NUMERIC";
//			} else if (field.getType().equals("timestamp")) {
//				sql += "TIMESTAMP";
//			}
//
//			// Default DtTimestamp
//			if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
//				sql += " DEFAULT CURRENT_TIMESTAMP";
//			}
//
//			// Nullable
//			if (field.getNullable().trim().equalsIgnoreCase("n")) {
//				sql += " NOT NULL";
//			}
//
//			// Add comments
//			if (!field.getDescription().trim().equals("")) {
//				fieldComments += "\n";
//				fieldComments += "COMMENT ON COLUMN ";
//				fieldComments += this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "."
//						+ tempTableName + "." + field.getName();
//				fieldComments += " IS ";
//				fieldComments += "'";
//				fieldComments += field.getDescription();
//				fieldComments += "'";
//				fieldComments += ";";
//			}
//
//			counter++;
//		}
//		sql += "\n";
//		sql += ")";
//		sql += "\n";
//
//		sql += ";";
//		sql += "\n";
//
//		sql += fieldComments;
//		sql += "\n";
//		sql += "\n";
//
//		// Grants
//
//		return sql;
//	}
//
//
//
//	private String getCreateStatementSQLite() {
//		String sql = "";
//		String tempTableName = this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix() + this.getTable().getName();
//
//
//		sql += "CREATE TABLE " + tempTableName;
//		sql += "\n";
//		sql += "(";
//		sql += "\n";
//
//		int counter = 1;
//		for (MetadataField field : this.getTable().getFields()) {
//			if (counter > 1) {
//				sql += ",";
//				sql += "\n";
//			}
//
//			sql += "\t";
//			sql += field.getName();
//
//			int tabNumber = 1;
//			if (field.getName().length() >= 8) {
//				tabNumber = (int) (4 - Math.ceil(field.getName().length() / 8));
//			} else {
//				tabNumber = 4;
//			}
//
//			for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
//				sql += "\t";
//			}
//
//			// Data Types
//			if (field.getType().equals("string")) {
//				sql += "TEXT";
//			} else if (field.getType().equals("flag")) {
//				sql += "TEXT";
//			} else if (field.getType().equals("number")) {
//				sql += "NUMERIC";
//			} else if (field.getType().equals("timestamp")) {
//				sql += "TEXT";
//			}
//
//			// Nullable
//			if (field.getNullable().trim().equalsIgnoreCase("n")) {
//				sql += " NOT NULL";
//			}
//
//			// Default DtTimestamp
//			if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
//				//sql += " DEFAULT CURRENT_TIMESTAMP";
//				sql += " DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))";
//			}
//
//			counter++;
//		}
//		sql += "\n";
//		sql += ");";
//		sql += "\n";
//
//		return sql;
//	}
//
//
//	// Getters and Setters
//	public MetadataTable getTable() {
//		return table;
//	}
//
//	public void setTable(MetadataTable table) {
//		this.table = table;
//	}
//
//	public MetadataRepository getMetadataRepository() {
//		return metadataRepository;
//	}
//
//	public void setMetadataRepository(MetadataRepository metadataRepository) {
//		this.metadataRepository = metadataRepository;
//	}

}