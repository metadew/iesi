package io.metadew.iesi.gcp.connection.pubsub;

public class PubsubService {

    private static PubsubService INSTANCE;

    public synchronized static PubsubService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PubsubService();
        }
        return INSTANCE;
    }

    //TODO improve logging
    //TODO automatically find subscriptions and display error
    private PubsubService () {

    }

    public void createTopic(String projectName, String topicName) {
        Topic topic = new Topic(projectName,topicName);

        try {
            if (!topic.exists()) {
                topic.create();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTopic(String projectName, String topicName){
        Topic topic = new Topic(projectName,topicName);

        try {
            if (topic.exists()) {
                topic.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createSubscription(String projectName, String topicName, String subscriptionName) {
        Topic topic = new Topic(projectName,topicName);
        Subscription subscription = new Subscription(topic,subscriptionName);

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

    public void deleteSubscription(String projectName, String topicName, String subscriptionName) {
        Topic topic = new Topic(projectName,topicName);
        Subscription subscription = new Subscription(topic,subscriptionName);

        try {
            if (subscription.exists()) {
                subscription.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}