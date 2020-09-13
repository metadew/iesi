package io.metadew.iesi.connection.publisher.gcp;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import lombok.Data;

import java.io.IOException;

@Data
public class GCPPublisher implements io.metadew.iesi.connection.publisher.Publisher {

    private final Publisher publisher;

    public GCPPublisher(String projectId, String topicId) throws IOException {
        TopicName topicName = TopicName.of(projectId, topicId);
        this.publisher = Publisher.newBuilder(topicName).build();
    }
}
