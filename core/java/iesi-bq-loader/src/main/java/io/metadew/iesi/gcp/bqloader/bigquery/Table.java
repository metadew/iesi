package io.metadew.iesi.gcp.bqloader.bigquery;

import com.google.cloud.bigquery.*;

public class Table {

    private Dataset dataset;
    private String name;

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Table (Dataset dataset, String name) {
        this.setDataset(dataset);
        this.setName(name);
    }

    public void create() {
        BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
        TableId tableId = TableId.of(this.getDataset().getName(), this.getName());

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
        TableId tableId = TableId.of(this.getDataset().getProject(), this.getDataset().getName(), this.getName());
        boolean deleted = bigquery.delete(tableId);
        if (deleted) {
            System.out.println("Table was deleted.");
        } else {
            System.out.println("Table was not found.");
        }
    }
}
