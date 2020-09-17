package io.metadew.iesi.gcp.connection.pubsub;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.ProjectSubscriptionName;

import java.io.IOException;

public class Subscription {

    private Topic topic;
    private String name;
    private ProjectSubscriptionName subscriptionName;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ProjectSubscriptionName getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(ProjectSubscriptionName subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public Subscription(Topic topic, String name) {
        this.setName(name);
        this.setTopic(topic);
        this.setSubscriptionName(ProjectSubscriptionName.of(this.getTopic().getProject(), this.getName()));
    }

    public void create() throws IOException {
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.createSubscription(this.getSubscriptionName(), this.getTopic().getTopicName(), PushConfig.getDefaultInstance(), 0);
        }
    }

    public void delete() throws IOException {
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.deleteSubscription(this.getSubscriptionName());
        }
    }

    public boolean exists() {
        boolean result = false;
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.getSubscription(this.getSubscriptionName());
            result = true;
        } catch (Exception e) { 
            result = false;
        }
        return result;
    }

}
