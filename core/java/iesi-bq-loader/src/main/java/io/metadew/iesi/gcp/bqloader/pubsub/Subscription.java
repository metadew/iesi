package io.metadew.iesi.gcp.bqloader.pubsub;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.TopicName;

public class Subscription {

    private Topic topic;
    private String name;

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

    public Subscription(Topic topic, String name) {
        this.setName(name);
        this.setTopic(topic);
    }

    public void create() {
        TopicName topic = TopicName.of(this.getTopic().getProject(), this.getTopic().getName());
        ProjectSubscriptionName subscription = ProjectSubscriptionName.of(this.getTopic().getProject(), this.getName());
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 20);
        } catch (Exception e) {
            throw new RuntimeException("Subscription creation failed for: " + this.getName());
        }
    }

    public void delete() {
        TopicName topic = TopicName.of(this.getTopic().getProject(), this.getTopic().getName());
        ProjectSubscriptionName subscription = ProjectSubscriptionName.of(this.getTopic().getProject(), this.getName());
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.deleteSubscription(subscription);
        } catch (Exception e) {
            throw new RuntimeException("Subscription deletion failed for: " + this.getName());
        }
    }

    public boolean exists() {
        boolean result = false;
        TopicName topic = TopicName.of(this.getTopic().getProject(), this.getTopic().getName());
        ProjectSubscriptionName subscription = ProjectSubscriptionName.of(this.getTopic().getProject(), this.getName());
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            subscriptionAdminClient.getSubscription(subscription);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

}
