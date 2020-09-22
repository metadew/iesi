package io.metadew.iesi.gcp.pubsub.launch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.common.configuration.Code;
import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.connection.pubsub.PubsubConnection;
import io.metadew.iesi.gcp.pubsub.common.PubsubService;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSubscriptionSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubTopicSpec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.*;

@Command(
        name = "create"
)
public class PubsubSpecCreateCommand implements Runnable {
    @Option(names = {"-n", "--name"}, required = true, description = "the pubsup name to create")
    private String pubsubName;

    @Option(names = {"-p", "--project"}, description = "the project where to create the spec")
    private String projectName;

    @Parameters
    private List<Path> files;

    @Override
    public void run() {
        String whichProject = Mount.getInstance().getProjectName(projectName);

        //Read the spec
        if (files != null) {
            files.forEach(path -> Spec.getInstance().readSpec(path));
        }

        //Create the pubsub service
        PubsubService.getInstance().init(whichProject,pubsubName);

        //Run through and apply the spec
        if (PubsubService.getInstance().getPubsubSpec() != null) {
            //Create the topics and subscriptions
            for (PubsubTopicSpec pubsubTopicSpec : PubsubService.getInstance().getPubsubSpec().getTopics()) {
                PubsubConnection.getInstance().createTopic(whichProject,pubsubTopicSpec.getName());
                if ( pubsubTopicSpec.getSubscriptions() != null) {
                    for (PubsubSubscriptionSpec pubsubSubscriptionSpec : pubsubTopicSpec.getSubscriptions()) {
                        PubsubConnection.getInstance().createSubscription(whichProject, pubsubTopicSpec.getName(), pubsubSubscriptionSpec.getName());
                    }
                }
            }
        }

    }
}
