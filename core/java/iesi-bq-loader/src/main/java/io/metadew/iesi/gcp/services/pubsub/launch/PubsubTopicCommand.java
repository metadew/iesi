package io.metadew.iesi.gcp.services.pubsub.launch;

import picocli.CommandLine.Command;

@Command(
        name = "topic",
        subcommands = {
                PubsubTopicCreateCommand.class,
                PubsubTopicDeleteCommand.class
        }
)
public class PubsubTopicCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the pubsub topics");

    }
}
