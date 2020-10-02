package io.metadew.iesi.gcp.connection.bigquery;

import com.google.cloud.bigquery.*;

public class BigqueryTable {

    private BigqueryDataset bigqueryDataset;
    private String name;

    public BigqueryDataset getBigqueryDataset() {
        return bigqueryDataset;
    }

    public void setBigqueryDataset(BigqueryDataset bigqueryDataset) {
        this.bigqueryDataset = bigqueryDataset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigqueryTable(BigqueryDataset bigqueryDataset, String name) {
        this.setBigqueryDataset(bigqueryDataset);
        this.setName(name);
    }

    public void create() {
        TableId tableId = TableId.of(this.getBigqueryDataset().getName(), this.getName());

        // Table field definition
        com.google.cloud.bigquery.Field field1 = com.google.cloud.bigquery.Field.of("foo", LegacySQLTypeName.STRING);
        com.google.cloud.bigquery.Field field2 = com.google.cloud.bigquery.Field.of("bar", LegacySQLTypeName.STRING);

        // Table schema definition
        com.google.cloud.bigquery.Schema schema = com.google.cloud.bigquery.Schema.of(field1,field2);

        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();

        com.google.cloud.bigquery.Table table = BigqueryConnection.getInstance().getService().create(tableInfo);
    }

    public void create(Schema schema) {
        TableId tableId = TableId.of(this.getBigqueryDataset().getName(), this.getName());

        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();

        com.google.cloud.bigquery.Table table = BigqueryConnection.getInstance().getService().create(tableInfo);
    }


    public void create2() {
        TableId tableId = TableId.of(this.getBigqueryDataset().getName(), "test");

        Schema schema =
                Schema.of(
                        Field.of("RUN_ID", StandardSQLTypeName.STRING),
                        Field.of("PRC_ID", StandardSQLTypeName.INT64),
                        Field.of("PARENT_PRC_ID", StandardSQLTypeName.INT64),
                        Field.of("SCRIPT_ID", StandardSQLTypeName.STRING),
                        Field.of("SCRIPT_NM", StandardSQLTypeName.STRING),
                        Field.of("SCRIPT_VRS_NB", StandardSQLTypeName.INT64),
                        Field.of("ENV_NM", StandardSQLTypeName.STRING),
                        Field.of("ST_NM", StandardSQLTypeName.STRING),
                        Field.of("STRT_TMS", StandardSQLTypeName.TIMESTAMP),
                        Field.of("END_TMS", StandardSQLTypeName.TIMESTAMP),
                        // create the nested and repeated field
                        Field.newBuilder(
                                "ACTIONS",
                                StandardSQLTypeName.STRUCT,
                                Field.of("ACTION_NM", StandardSQLTypeName.STRING)
                                )
                                .setMode(Field.Mode.REPEATED)
                                .build());

        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();

        com.google.cloud.bigquery.Table table = BigqueryConnection.getInstance().getService().create(tableInfo);
    }

    public void delete() {
        TableId tableId = TableId.of(this.getBigqueryDataset().getProject(), this.getBigqueryDataset().getName(), this.getName());
        boolean deleted = BigqueryConnection.getInstance().getService().delete(tableId);
        if (deleted) {
            System.out.println("Table was deleted.");
        } else {
            System.out.println("Table was not found.");
        }
    }
}
