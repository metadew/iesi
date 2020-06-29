package io.metadew.iesi.metadata.definition.template.matcher.value;

public class MatcherValueJsonComponent {

    public enum Field {
        TYPE_KEY("type");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

}
