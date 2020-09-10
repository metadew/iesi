package io.metadew.iesi.metadata.definition.template.matcher.value;

public class MatcherFixedValueJsonComponent {

    public enum Field {
        TYPE("fixed"),
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
