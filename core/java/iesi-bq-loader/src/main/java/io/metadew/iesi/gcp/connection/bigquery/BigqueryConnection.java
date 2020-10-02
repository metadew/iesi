package io.metadew.iesi.gcp.connection.bigquery;

import com.google.cloud.bigquery.*;
import io.metadew.iesi.gcp.configuration.bigquery.TableConfiguration;

import java.util.List;
import java.util.Map;

public class BigqueryConnection {

    private static BigqueryConnection INSTANCE;
    private static BigQuery bigquery;

    public synchronized static BigqueryConnection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigqueryConnection();
        }
        return INSTANCE;
    }

    private BigqueryConnection() {
        bigquery = BigQueryOptions.getDefaultInstance().getService();
    }

    //Bigquery instance
    public BigQuery getService() {
        return bigquery;
    }

    //Dataset
    public void createDataset(String projectName, String datasetName) {
        BigqueryDataset bigqueryDataset = new BigqueryDataset(projectName, datasetName);

        try {
            bigqueryDataset.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigqueryDataset getDataset(String projectName, String datasetName) {
        return new BigqueryDataset(projectName, datasetName);
    }

    public void deleteDataset(String projectName, String datasetName) {
        BigqueryDataset bigqueryDataset = new BigqueryDataset(projectName, datasetName);

        try {
            bigqueryDataset.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean datasetExists(String datasetName) {
        try {
            Dataset dataset = bigquery.getDataset(DatasetId.of(datasetName));
            if (dataset != null && dataset.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (BigQueryException e) {
            return false;
        }
    }

    //Table
    public void createTable(String projectName, String datasetName, TableConfiguration tableConfiguration) {
        BigqueryDataset bigqueryDataset = new BigqueryDataset(projectName, datasetName);
        BigqueryTable bigqueryTable = new BigqueryTable(bigqueryDataset, tableConfiguration.getTableName());

        try {
            if (!BigqueryConnection.getInstance().datasetExists(datasetName)) {
                BigqueryConnection.getInstance().createDataset(projectName,datasetName);
            }
            bigqueryTable.create(tableConfiguration.getSchema());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteTable(String projectName, String datasetName, TableConfiguration tableConfiguration) {
        BigqueryDataset bigqueryDataset = new BigqueryDataset(projectName, datasetName);
        BigqueryTable bigqueryTable = new BigqueryTable(bigqueryDataset, tableConfiguration.getTableName());

        try {
            bigqueryTable.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tableInsertRows(
            String datasetName, String tableName, Map<String, Object> rowContent) {
        try {
            // Initialize client that will be used to send requests. This client only needs to be created
            // once, and can be reused for multiple requests.
            BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

            // Get table
            TableId tableId = TableId.of(datasetName, tableName);

            // Inserts rowContent into datasetName:tableId.
            InsertAllResponse response =
                    bigquery.insertAll(
                            InsertAllRequest.newBuilder(tableId)
                                    // More rows can be added in the same RPC by invoking .addRow() on the builder.
                                    // You can also supply optional unique row keys to support de-duplication
                                    // scenarios.
                                    .addRow(rowContent)
                                    .build());

            if (response.hasErrors()) {
                // If any of the insertions failed, this lets you inspect the errors
                for (Map.Entry<Long, List<BigQueryError>> entry : response.getInsertErrors().entrySet()) {
                    System.out.println("Response error: \n" + entry.getValue());
                }
            }
            System.out.println("Rows successfully inserted into table");
        } catch (BigQueryException e) {
            System.out.println("Insert operation not performed \n" + e.toString());
        }
    }
}

