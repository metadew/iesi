package io.metadew.iesi.gcp.services.pubsub.launch;

import picocli.CommandLine.Command;

@Command(
        name = "subscription",
        subcommands = {
                PubsubSubscriptionCreateCommand.class,
                PubsubSubscriptionDeleteCommand.class
        }
)
public class PubsubSubscriptionCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Manage the pubsub subscriptions");

    }
}
