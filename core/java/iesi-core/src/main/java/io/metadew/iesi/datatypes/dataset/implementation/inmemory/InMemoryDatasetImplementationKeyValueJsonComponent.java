package io.metadew.iesi.datatypes.dataset.implementation.inmemory;

public class InMemoryDatasetImplementationKeyValueJsonComponent {

    public enum Field {
        KEY_KEY("key"),
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
