package io.metadew.iesi.metadata.definition.environment;

public class EnvironmentParameterJsonComponent {
    public enum Field {
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
