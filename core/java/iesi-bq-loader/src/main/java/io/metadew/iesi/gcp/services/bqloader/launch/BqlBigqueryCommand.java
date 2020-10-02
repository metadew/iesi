package io.metadew.iesi.gcp.services.bqloader.launch;

import picocli.CommandLine.Command;

@Command(
        name = "bigquery",
        subcommands = {
                BqlBigqueryCreateCommand.class,
                BqlBigqueryDeleteCommand.class
        }
)
public class BqlBigqueryCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the biggquery setup");

    }
}
