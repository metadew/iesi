package io.metadew.iesi.metadata.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.metadata.configuration.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;

@JsonDeserialize(using = MetadataJsonComponent.Deserializer.class)
@Data
public abstract class Metadata<T extends MetadataKey> {

    private final T metadataKey;

}
