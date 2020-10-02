package io.metadew.iesi.gcp.services.pubsub.launch;

import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.connection.pubsub.PubsubConnection;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "create"
)
public class PubsubSubscriptionCreateCommand implements Runnable {
    @Option(names = {"-n", "--name"}, required = true, description = "the subscription to create")
    private String subscriptionName;

    @Option(names = {"-t", "--topic"}, required = true, description = "the topic for which to create the subscription")
    private String topicName;

    @Option(names = {"-p", "--project"}, description = "the project where to create the subscription")
    private String projectName;

    @Override
    public void run() {
        String whichProject = Mount.getInstance().getProjectName(projectName);
        PubsubConnection.getInstance().createSubscription(whichProject,topicName,subscriptionName);
    }
}
