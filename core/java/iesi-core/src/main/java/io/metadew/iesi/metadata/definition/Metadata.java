package io.metadew.iesi.metadata.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

@JsonDeserialize(using = MetadataJsonComponent.Deserializer.class)
@EqualsAndHashCode
public abstract class Metadata<T extends MetadataKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final T metadataKey;

    public Metadata(T metadataKey) {
        this.metadataKey = metadataKey;
    }

    public T getMetadataKey() {
        return metadataKey;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        Field[] fields = this.getClass().getDeclaredFields();
        Method[] methods = this.getClass().getMethods();
        stringBuilder.append("key: ").append(getMetadataKey().toString()).append(", ");

        //print field names paired with their values
        stringBuilder.append(Arrays.stream(fields).map(field -> {
            String value = Arrays.stream(methods).filter(method -> isGetter(method, field.getName()))
                    .findFirst()
                    .map(method -> {
                        try {
                            return method.invoke(this)==null ? "<null>" : method.invoke(this).toString();
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return "<private>";
                        }
                    }).orElse("<private>");
            return field.getName() + ": " + value;
        }).collect(Collectors.joining(", ")));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    private boolean isGetter(Method method, String fieldName) {
        if (Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)) &&
                    !method.getReturnType().equals(void.class))
                return true;
            if (method.getName().matches("^is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)) &&
                    method.getReturnType().equals(boolean.class))
                return true;
        }
        return false;
    }
}
