package io.metadew.iesi.gcp.services.bigquery.launch;

import picocli.CommandLine.Command;

@Command(
        name = "scriptexecution",
        subcommands = {
                BigqueryScriptexecutionCreateCommand.class,
                BigqueryScriptexecutionDeleteCommand.class
        }
)
public class BigqueryScriptexecutionCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the bigquery scriptexecution table");

    }
}

