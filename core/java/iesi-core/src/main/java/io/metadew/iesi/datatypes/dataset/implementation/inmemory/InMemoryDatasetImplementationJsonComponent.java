package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

public class InMemoryDatasetImplementationJsonComponent {

    public enum Field {
        TYPE("in_memory"),
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
