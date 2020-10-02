package io.metadew.iesi.gcp.services.bqloader.launch;

import picocli.CommandLine.Command;

@Command(
        name = "pubsub",
        subcommands = {
                BqlPubsubCreateCommand.class,
                BqlPubsubDeleteCommand.class,
                BqlPubsubPublishCommand.class
        }
)
public class BqlPubsubCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the pubsub setup");

    }
}
