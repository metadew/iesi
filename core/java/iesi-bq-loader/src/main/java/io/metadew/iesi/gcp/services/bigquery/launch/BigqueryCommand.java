package io.metadew.iesi.gcp.services.bigquery.launch;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
        name = "bigquery",
        subcommands = {
                BigqueryDatasetCommand.class,
                BigqueryScriptexecutionCommand.class,
                BigqueryScriptresultCommand.class
        }
)
public class BigqueryCommand implements Runnable {
    public static void main(String[] args) {
        new CommandLine(new BigqueryCommand()).execute(args);
    }

    @Override
    public void run() {
        System.out.println("The bigquery command");
    }
}