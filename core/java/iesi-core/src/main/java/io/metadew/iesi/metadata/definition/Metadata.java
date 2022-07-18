package io.metadew.iesi.metadata.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;

import java.io.Serializable;

@JsonDeserialize(using = MetadataJsonComponent.Deserializer.class)
@Data
public abstract class Metadata<T extends MetadataKey> implements Serializable {

    private final T metadataKey;

}
