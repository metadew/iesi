package io.metadew.iesi.gcp.services.bqloader.launch;

import io.metadew.iesi.gcp.common.configuration.Configuration;
import io.metadew.iesi.gcp.connection.pubsub.Subscription;
import io.metadew.iesi.gcp.connection.pubsub.Topic;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "publish"
)
public class BqlPubsubPublishCommand implements Runnable {
    @Option(names = {"-m", "--message"}, required = true, description = "the message to publish")
    private String message;

    @Override
    public void run() {
        //TODO add validation of configurations - move to service

        //Define the topic and subscription
        Topic topic = new Topic(Configuration.getInstance().getProperty("iesi.gcp.bql.project").orElse("").toString(),Configuration.getInstance().getProperty("iesi.gcp.bql.topic").orElse("").toString());
        Subscription subscription = new Subscription(topic,Configuration.getInstance().getProperty("iesi.gcp.bql.subscription").orElse("").toString());

        try {
            topic.publish(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

