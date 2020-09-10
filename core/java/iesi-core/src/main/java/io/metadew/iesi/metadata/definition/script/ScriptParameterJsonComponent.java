package io.metadew.iesi.metadata.definition.script;

public class ScriptParameterJsonComponent {

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
