package io.metadew.iesi.metadata.definition.template.matcher.value;

public class MatcherAnyValueJsonComponent {

    public enum Field {
        TYPE("any");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

}
