package io.metadew.iesi.gcp.services.pubsub.launch;

import picocli.CommandLine.Command;

@Command(
        name = "spec",
        subcommands = {
                PubsubSpecCreateCommand.class,
                PubsubSpecDeleteCommand.class
        }
)
public class PubsubSpecCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the pubsub specs");
    }
}
