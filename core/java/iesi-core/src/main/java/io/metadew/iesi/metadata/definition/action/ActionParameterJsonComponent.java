package io.metadew.iesi.metadata.definition.action;

public class ActionParameterJsonComponent {

    public enum Field {
        PARAMETER_NAME_KEY("name"),
        PARAMETER_VALUE_KEY("value");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }
}
