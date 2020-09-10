package io.metadew.iesi.metadata.definition.template.matcher.value;

public class MatcherTemplateJsonComponent {

    public enum Field {
        TYPE("template"),
        TEMPLATE_NAME_KEY("templateName"),
        TEMPLATE_VERSION_KEY("templateVersion");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

}
