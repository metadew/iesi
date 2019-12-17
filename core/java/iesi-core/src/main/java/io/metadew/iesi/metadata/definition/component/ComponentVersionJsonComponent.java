package io.metadew.iesi.metadata.definition.component;

public class ComponentVersionJsonComponent {

    public enum Field {
        NUMBER_KEY("number"),
        DESCRIPTION_KEY("description"),
        BUILDS_KEY("builds");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }
}
