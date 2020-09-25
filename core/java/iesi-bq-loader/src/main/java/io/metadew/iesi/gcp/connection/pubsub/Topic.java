package io.metadew.iesi.gcp.connection.pubsub;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.TopicName;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Topic {

    private String project;
    private String name;
    private TopicName topicName;

    public void setProject(String project) {
        this.project = project;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public TopicName getTopicName() {
        return topicName;
    }

    public void setTopicName(TopicName topicName) {
        this.topicName = topicName;
    }

    public Topic(String project, String name) {
        this.setName(name);
        this.setProject(project);
        this.setTopicName(TopicName.of(this.getProject(), this.getName()));
    }

    public void create() throws IOException {
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            topicAdminClient.createTopic(this.getTopicName());
        }
    }

    public void delete() throws IOException {
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            topicAdminClient.deleteTopic(this.getTopicName());
        }
    }

    public boolean exists() {
        boolean result;
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            topicAdminClient.getTopic(this.getTopicName());
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public String publish(String message) throws InterruptedException, IOException {
        Publisher publisher = null;
        final String[] publishedMessageId = {""};
        try {
            publisher = Publisher.newBuilder(this.getTopicName()).build();
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            ApiFutures.addCallback(messageIdFuture, new ApiFutureCallback<String>() {
                public void onSuccess(String messageId) {
                    publishedMessageId[0] = messageId;
                }

                public void onFailure(Throwable t) {
                    System.out.println("failed to publish: " + t);
                }
            }, MoreExecutors.directExecutor());
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
        return publishedMessageId[0];

    }
}
