package io.metadew.iesi.common.configuration.publisher;

import io.metadew.iesi.connection.Publisher;
import lombok.Data;

import java.io.IOException;

@Data
public abstract class PublisherConfiguration<T extends Publisher> {

    private final String type;

    public abstract T convert() throws IOException;
}
