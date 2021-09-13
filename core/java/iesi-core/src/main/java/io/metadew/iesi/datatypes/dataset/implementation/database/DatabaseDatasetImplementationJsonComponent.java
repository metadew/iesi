package io.metadew.iesi.datatypes.dataset.implementation.database;

public class DatabaseDatasetImplementationJsonComponent {

    public enum Field {
        KEY_VALUES_KEY("key_values");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }
}
