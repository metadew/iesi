package io.metadew.iesi.gcp.launch;

import io.metadew.iesi.gcp.services.bigquery.launch.BigqueryCommand;
import io.metadew.iesi.gcp.services.bqloader.launch.BqlCommand;
import io.metadew.iesi.gcp.services.dlp.launch.DlpCommand;
import io.metadew.iesi.gcp.services.pubsub.launch.PubsubCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
        subcommands = {
                BigqueryCommand.class,
                BqlCommand.class,
                DlpCommand.class,
                PubsubCommand.class,
                SpecCommand.class
        }
)
public class RootCommand implements Runnable {
    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2-gcp.xml");
        new CommandLine(new RootCommand()).execute(args);
    }

    @Override
    public void run() {
        System.out.println("The iesi.gcp root command");
    }
}