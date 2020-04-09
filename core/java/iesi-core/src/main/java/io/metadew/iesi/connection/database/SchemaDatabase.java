package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;

@Setter
public abstract class SchemaDatabase extends Database {

    private String schema;

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection, String schema) {
        super(schemaDatabaseConnection);
        schemaDatabaseConnection.setSchema(schema);
        this.schema = schema;
    }

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection) {
        this(schemaDatabaseConnection, null);
    }

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection, int initialPoolSize, int maximalPoolSize,  String schema) {
        super(schemaDatabaseConnection, initialPoolSize, maximalPoolSize);
        schemaDatabaseConnection.setSchema(schema);
        this.schema = schema;
    }

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection, int initialPoolSize, int maximalPoolSize) {
        this(schemaDatabaseConnection, initialPoolSize, maximalPoolSize, null);
    }
   
    public String getCreateStatement(MetadataTable table) {
        StringBuilder createQuery = new StringBuilder();
        // add schema to table name
        String tableName = getSchema().map(schema -> schema + "." + table.getName()).orElse(table.getName());

        createQuery.append("CREATE TABLE ").append(tableName).append("\n(\n");
        int counter = 1;
        for (Map.Entry<String, MetadataField> field : table.getFields().entrySet()) {
            if (counter > 1) {
                createQuery.append(",\n");
            }
            createQuery.append("\t").append(field.getKey());

            int tabNumber = 1;
            if (field.getKey().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil((double) field.getKey().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                createQuery.append("\t");
            }

            createQuery.append(toQueryString(field.getValue()));
			/*
			 * TODO create comment syntax inside subclasses returning stringbuilder rather
			 * than just a boolean
			 *
			 * if (addComments() && field.getDescription().isPresent()) {
			 * fieldComments.append("\nCOMMENT ON COLUMN ").append(tableName).append(".").
			 * append(field.getScriptName())
			 * .append(" IS '").append(field.getDescription().get()).append("';"); }
			 */
            counter++;
        }

        getPrimaryKeyConstraints(table).ifPresent(primaryKeysConstraint -> createQuery.append(",\n").append(primaryKeysConstraint));
        createQuery.append("\n)").append(createQueryExtras()).append(";");
        //createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
    }

    public String getDeleteStatement(MetadataTable table) {
        return "delete from " + getSchema().map(schema -> schema + "." + table.getName()).orElse(table.getName()) + ";";
    }

    public String getDropStatement(MetadataTable table) {
        return "drop table " + getSchema().map(schema -> schema + "." + table.getName()).orElse(table.getName()) + ";";
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

}
