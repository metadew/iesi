package io.metadew.iesi.gcp.services.bigquery.launch;

import picocli.CommandLine.Command;

@Command(
        name = "dataset",
        subcommands = {
                BigqueryDatasetCreateCommand.class,
                BigqueryDatasetDeleteCommand.class
        }
)
public class BigqueryDatasetCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the bigquery datasets");

    }
}

