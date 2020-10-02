package io.metadew.iesi.gcp.services.bigquery.launch;

import picocli.CommandLine.Command;

@Command(
        name = "scriptresult",
        subcommands = {
                BigqueryScriptresultCreateCommand.class,
                BigqueryScriptresultDeleteCommand.class
        }
)
public class BigqueryScriptresultCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the bigquery scriptresult table");

    }
}

