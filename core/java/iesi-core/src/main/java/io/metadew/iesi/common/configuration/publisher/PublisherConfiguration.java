package io.metadew.iesi.common.configuration.publisher;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.connection.Publisher;
import lombok.Data;

import java.io.IOException;

@Data
@JsonDeserialize(using = PublisherConfigurationJsonComponent.Deserializer.class)
public abstract class PublisherConfiguration<T extends Publisher> {

    private final String type;

    public abstract T convert() throws IOException;
}
