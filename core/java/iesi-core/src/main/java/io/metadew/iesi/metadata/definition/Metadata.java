package io.metadew.iesi.metadata.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

@JsonDeserialize(using = MetadataJsonComponent.Deserializer.class)
@Data
public abstract class Metadata<T extends MetadataKey> {

    private final T metadataKey;

}
