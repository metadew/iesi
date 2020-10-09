package io.metadew.iesi.metadata.definition.security;

public class SecurityGroupJsonComponent {

    public enum Field {
        SECURITY_GROUP_NAME("security_group");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

}
