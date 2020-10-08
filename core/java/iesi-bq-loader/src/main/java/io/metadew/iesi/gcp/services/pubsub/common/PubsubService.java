package io.metadew.iesi.gcp.services.pubsub.common;

import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.connection.pubsub.PubsubConnection;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSubscriptionSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubTopicSpec;
import lombok.Getter;

@Getter
public class PubsubService {
    private static PubsubService INSTANCE;
    private PubsubSpec pubsubSpec;
    private String projectName;

    public synchronized static PubsubService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PubsubService();
        }
        return INSTANCE;
    }

    private PubsubService () {

    }

    public void init (String projectName, String dlpName) {
        this.projectName = projectName;
        pubsubSpec = null;
        for (PubsubSpec entry : Spec.getInstance().getGcpSpec().getPubsub()) {
            if (entry.getName().equalsIgnoreCase(dlpName)) {
                pubsubSpec = entry;
            }
        }
    }

    public void create() {
        if (pubsubSpec != null) {
            //Create the topics and subscriptions
            for (PubsubTopicSpec pubsubTopicSpec : PubsubService.getInstance().getPubsubSpec().getTopics()) {
                PubsubConnection.getInstance().createTopic(projectName,pubsubTopicSpec.getName());
                if ( pubsubTopicSpec.getSubscriptions() != null) {
                    for (PubsubSubscriptionSpec pubsubSubscriptionSpec : pubsubTopicSpec.getSubscriptions()) {
                        PubsubConnection.getInstance().createSubscription(projectName, pubsubTopicSpec.getName(), pubsubSubscriptionSpec.getName());
                    }
                }
            }
        }
    }

    public void delete() {
        if (pubsubSpec != null) {
            //Delete the topics and subscriptions
            for (PubsubTopicSpec pubsubTopicSpec : PubsubService.getInstance().getPubsubSpec().getTopics()) {
                if ( pubsubTopicSpec.getSubscriptions() != null) {
                    for (PubsubSubscriptionSpec pubsubSubscriptionSpec : pubsubTopicSpec.getSubscriptions()) {
                        PubsubConnection.getInstance().deleteSubscription(projectName, pubsubTopicSpec.getName(), pubsubSubscriptionSpec.getName());
                    }
                }
                PubsubConnection.getInstance().deleteTopic(projectName,pubsubTopicSpec.getName());
            }
        }
    }
}