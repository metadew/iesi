package io.metadew.iesi.gcp.services.bqloader.launch;

import io.metadew.iesi.gcp.common.configuration.Configuration;
import io.metadew.iesi.gcp.connection.pubsub.Subscription;
import io.metadew.iesi.gcp.connection.pubsub.Topic;
import picocli.CommandLine.Command;

@Command(
        name = "create"
)
public class BqlPubsubCreateCommand implements Runnable {
    @Override
    public void run() {
        //TODO add validation of configurations - move to service

        //Define the topic and subscription
        Topic topic = new Topic(Configuration.getInstance().getProperty("iesi.gcp.bql.project").orElse("").toString(),Configuration.getInstance().getProperty("iesi.gcp.bql.topic").orElse("").toString());
        Subscription subscription = new Subscription(topic,Configuration.getInstance().getProperty("iesi.gcp.bql.subscription").orElse("").toString());

        try {
            if (!topic.exists()) {
                topic.create();
            }

            if (!subscription.exists()) {
                subscription.create();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

