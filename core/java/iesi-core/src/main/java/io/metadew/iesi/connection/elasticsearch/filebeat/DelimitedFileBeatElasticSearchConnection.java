package io.metadew.iesi.connection.database.connection.elasticsearch;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DelimitedFileBeatElasticSearchConnection extends FileBeatElasitcSearchConnection {

    private final static Logger LOGGER = LogManager.getLogger();
    private final String delimiter;
    private final String quoteCharacter;

    public DelimitedFileBeatElasticSearchConnection() {
        this(" ");
    }

    public DelimitedFileBeatElasticSearchConnection(String delimiter) {
        this(delimiter, "\"");
    }


    public DelimitedFileBeatElasticSearchConnection(String delimiter, String quoteCharacter) {
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;
    }


    @Override
    public void ingest(ElasticSearchDocument o) {
        LOGGER.info(o.getLoggingMarker(), toString(o));
    }

    @Override
    public void ingest(String string) {
        LOGGER.info(string);
    }


    public String toString(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        Method[] methods = o.getClass().getDeclaredMethods();

        //print field names paired with their values
        return Arrays.stream(fields)
                .map(field -> Arrays.stream(methods)
                        .filter(method -> isGetter(method, field.getName()))
                        .findFirst()
                        .map(method -> {
                            try {
                                if (method.invoke(o) == null) {
                                    return "";
                                } else if (method.invoke(o) instanceof String) {
                                    return toString((String) method.invoke(o));
                                } else if (ClassUtils.isPrimitiveOrWrapper(method.invoke(o).getClass())) {
                                    return method.invoke(o).toString();
                                } else {
                                    return toString(method.invoke(o));
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                return "";
                            }
                        })
                        .orElse(""))
                .collect(Collectors.joining(delimiter));
    }

    public String toString(String string) {
        return quoteCharacter + string + quoteCharacter;
    }

    private boolean isGetter(Method method, String fieldName) {
        if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0) {
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
