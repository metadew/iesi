package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.TeradataDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

import java.util.List;

public class TeradataDatabase extends Database {


    public TeradataDatabase(TeradataDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return null;
    }

    @Override
    public String createQueryExtras() {
        return "";
    }

    @Override
    public boolean addComments() {
        return false;
    }

    @Override
    public String toQueryString(MetadataField field) {
        return "";
    }

    @Override
    public String toPrimaryKeyConstraint(MetadataTable metadataTable, List<MetadataField> primaryKeyMetadataFields) {return ""; }

    @Override
    public String toFieldName(MetadataField field) {
        StringBuilder result = new StringBuilder();
        result.append(field.getName());
        return result.toString();
    }
}
