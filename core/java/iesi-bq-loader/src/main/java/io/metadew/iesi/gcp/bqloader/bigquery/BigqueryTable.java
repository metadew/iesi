package io.metadew.iesi.gcp.bqloader.bigquery;

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
        BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
        TableId tableId = TableId.of(this.getBigqueryDataset().getName(), this.getName());

        // Table field definition
        com.google.cloud.bigquery.Field field1 = com.google.cloud.bigquery.Field.of("foo", LegacySQLTypeName.STRING);
        com.google.cloud.bigquery.Field field2 = com.google.cloud.bigquery.Field.of("bar", LegacySQLTypeName.STRING);

        // Table schema definition
        com.google.cloud.bigquery.Schema schema = com.google.cloud.bigquery.Schema.of(field1,field2);

        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();

        com.google.cloud.bigquery.Table table = bigquery.create(tableInfo);
    }

    public void delete() {
        BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
        TableId tableId = TableId.of(this.getBigqueryDataset().getProject(), this.getBigqueryDataset().getName(), this.getName());
        boolean deleted = bigquery.delete(tableId);
        if (deleted) {
            System.out.println("Table was deleted.");
        } else {
            System.out.println("Table was not found.");
        }
    }
}
