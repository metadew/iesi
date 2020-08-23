package io.metadew.iesi.common.configuration.publisher;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.connection.gcp.GCPPublisher;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(using = GCPPublisherConfigurationJsonComponent.Deserializer.class)
public class GCPPublisherConfiguration extends PublisherConfiguration<GCPPublisher> {

    private final String projectId;
    private final String topicId;

    public GCPPublisherConfiguration(String projectId, String topicId) {
        super(GCPPublisherConfigurationJsonComponent.Field.TYPE.value());
        this.projectId = projectId;
        this.topicId = topicId;
    }

    public GCPPublisher convert() throws IOException {
        return new GCPPublisher(projectId, topicId);
    }

}
