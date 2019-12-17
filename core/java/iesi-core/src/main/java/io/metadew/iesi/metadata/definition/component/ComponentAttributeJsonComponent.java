package io.metadew.iesi.metadata.definition.component;

public class ComponentAttributeJsonComponent {

    public enum Field {
        NAME_KEY("name"),
        ENVIRONMENT_KEY("environment"),
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
