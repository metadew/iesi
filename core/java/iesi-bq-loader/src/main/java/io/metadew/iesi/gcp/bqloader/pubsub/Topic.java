package io.metadew.iesi.gcp.bqloader.pubsub;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.TopicName;

public class Topic {

    private String project;
    private String name;

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

    public Topic(String project, String name) {
        this.setName(name);
        this.setProject(project);
    }

    public void create() {
        TopicName topic = TopicName.of(this.getProject(), this.getName());
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            topicAdminClient.createTopic(topic);
        } catch (Exception e) {
            throw new RuntimeException("Topic creation failed for: " + this.getName());
        }
    }

    public void delete() {
        TopicName topic = TopicName.of(this.getProject(), this.getName());
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            topicAdminClient.deleteTopic(topic);
        } catch (Exception e) {
            throw new RuntimeException("Topic deletion failed for: " + this.getName());
        }
    }

    public boolean exists() {
        boolean result = false;
        TopicName topic = TopicName.of(this.getProject(), this.getName());
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            topicAdminClient.getTopic(topic);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

}
