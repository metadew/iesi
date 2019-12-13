package io.metadew.iesi.metadata.definition.connection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import io.metadew.iesi.metadata.definition.MetadataJsonComponent;

import java.io.IOException;
import java.util.List;

public class ConnectionParameterJsonComponent {

    public enum Field {
        CONNECTION_NAME_KEY("connection_name"),
        ENVIRONMENT_TYPE_KEY("environment_name"),
        NAME_KEY("name"),
        VALUE_KEY("value");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }
}
