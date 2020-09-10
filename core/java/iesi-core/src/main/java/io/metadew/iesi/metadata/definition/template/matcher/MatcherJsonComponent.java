package io.metadew.iesi.metadata.definition.template.matcher;

public class MatcherJsonComponent {

    public enum Field {
        KEY_KEY("key"),
        MATCHER_VALUE_KEY("matcherValue");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }
}
