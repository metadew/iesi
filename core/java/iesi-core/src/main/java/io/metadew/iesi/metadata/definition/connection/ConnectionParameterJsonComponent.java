package io.metadew.iesi.metadata.definition.connection;

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
