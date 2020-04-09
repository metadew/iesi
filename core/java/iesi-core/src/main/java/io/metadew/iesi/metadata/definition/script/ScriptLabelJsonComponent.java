package io.metadew.iesi.metadata.definition.script;

public class ScriptLabelJsonComponent {

    public enum Field {
        NAME_KEY("name"),
        VALUE("value");


        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

}
