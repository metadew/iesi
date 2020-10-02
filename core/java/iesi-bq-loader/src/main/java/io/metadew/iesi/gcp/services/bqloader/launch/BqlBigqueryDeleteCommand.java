package io.metadew.iesi.gcp.services.bqloader.launch;

import io.metadew.iesi.gcp.common.configuration.Configuration;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryDataset;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryTable;
import picocli.CommandLine.Command;

@Command(
        name = "delete"
)
public class BqlBigqueryDeleteCommand implements Runnable {
    @Override
    public void run() {
        //TODO add validation of configurations - move to service

        BigqueryDataset bigqueryDataset = new BigqueryDataset(Configuration.getInstance().getProperty("iesi.gcp.bql.project").orElse("").toString(),Configuration.getInstance().getProperty("iesi.gcp.bql.dataset").orElse("").toString());
        BigqueryTable bigqueryTable = new BigqueryTable(bigqueryDataset, "");

        try {
            bigqueryTable.delete();
            bigqueryDataset.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

