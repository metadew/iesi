package io.metadew.iesi.datatypes.dataset.implementation;

public class DatasetImplementationJsonComponent {

    public enum Field {
        LABELS_KEY("labels"),
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
