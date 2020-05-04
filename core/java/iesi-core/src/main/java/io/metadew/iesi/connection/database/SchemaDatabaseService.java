package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

import java.util.Map;

public abstract class SchemaDatabaseService<T extends SchemaDatabase> extends DatabaseService<T> implements ISchemaDatabaseService<T> {

    public String getCreateStatement(T schemaDatabase, MetadataTable table) {
        StringBuilder createQuery = new StringBuilder();
        // add schema to table name
        String tableName = schemaDatabase.getSchema().map(schema -> schema + "." + table.getName()).orElse(table.getName());

        createQuery.append("CREATE TABLE ").append(tableName).append("\n(\n");
        int counter = 1;
        for (Map.Entry<String, MetadataField> field : table.getFields().entrySet()) {
            if (counter > 1) {
                createQuery.append(",\n");
            }
            createQuery.append("\t").append(fieldNameToQueryString(schemaDatabase, field.getKey()));

            int tabNumber = 1;
            if (field.getKey().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil((double) field.getKey().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                createQuery.append("\t");
            }

            createQuery.append(toQueryString(schemaDatabase, field.getValue()));
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

        getPrimaryKeyConstraints(schemaDatabase, table).ifPresent(primaryKeysConstraint -> createQuery.append(",\n").append(primaryKeysConstraint));
        createQuery.append("\n)").append(createQueryExtras(schemaDatabase)).append(";");
        //createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
    }

    public String getDeleteStatement(T schemaDatabase, MetadataTable table) {
        return "delete from " + schemaDatabase.getSchema().map(schema -> schema + "." + table.getName()).orElse(table.getName()) + ";";
    }

    public String getDropStatement(T schemaDatabase, MetadataTable table) {
        return "drop table " + schemaDatabase.getSchema().map(schema -> schema + "." + table.getName()).orElse(table.getName()) + ";";
    }

}