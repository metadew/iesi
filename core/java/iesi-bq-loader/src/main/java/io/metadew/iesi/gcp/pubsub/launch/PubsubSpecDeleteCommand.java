package io.metadew.iesi.gcp.pubsub.launch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.common.configuration.Code;
import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.connection.pubsub.PubsubService;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSubscriptionSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubTopicSpec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.*;

@Command(
        name = "delete"
)
public class PubsubSpecDeleteCommand implements Runnable {
    @Option(names = {"-p", "--project"}, description = "the project where to delete the spec")
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

        //Run through and apply the spec
        if (Spec.getInstance().get().containsKey(Code.PUBSUB.value())) {
            ObjectMapper objectMapper = new ObjectMapper();
            PubsubSpec pubsubSpec = objectMapper.convertValue(Spec.getInstance().get().get(Code.PUBSUB.value()), PubsubSpec.class);

            //Create the topics and subscriptions
            for (PubsubTopicSpec pubsubTopicSpec : pubsubSpec.getTopics()) {
                if ( pubsubTopicSpec.getSubscriptions() != null) {
                    for (PubsubSubscriptionSpec pubsubSubscriptionSpec : pubsubTopicSpec.getSubscriptions()) {
                        PubsubService.getInstance().deleteSubscription(whichProject, pubsubTopicSpec.getName(), pubsubSubscriptionSpec.getName());
                    }
                }
                PubsubService.getInstance().deleteTopic(whichProject,pubsubTopicSpec.getName());
            }
        }

    }
}
