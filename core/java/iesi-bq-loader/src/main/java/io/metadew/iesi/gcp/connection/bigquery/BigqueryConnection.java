package io.metadew.iesi.gcp.connection.bigquery;

import com.google.cloud.bigquery.*;

import java.util.List;
import java.util.Map;

public class BigqueryConnection {

    private static BigqueryConnection INSTANCE;

    public synchronized static BigqueryConnection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigqueryConnection();
        }
        return INSTANCE;
    }

    private BigqueryConnection() {

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

