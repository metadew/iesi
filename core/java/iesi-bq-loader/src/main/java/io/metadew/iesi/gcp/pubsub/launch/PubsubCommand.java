package io.metadew.iesi.gcp.pubsub.launch;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command (
        name = "pubsub",
        subcommands = {
                PubsubTopicCommand.class
        }
)
public class PubsubCommand implements Runnable {
    public static void main(String[] args) {
        new CommandLine(new PubsubCommand()).execute(args);
    }

    @Override
    public void run() {
        System.out.println("The pubsub command");
    }
}