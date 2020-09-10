package io.metadew.iesi.metadata.definition.script;

public class ScriptVersionJsonComponent {

    public enum Field {
        NUMBER_KEY("number"),
        DESCRIPTION_KEY("description");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }
}
