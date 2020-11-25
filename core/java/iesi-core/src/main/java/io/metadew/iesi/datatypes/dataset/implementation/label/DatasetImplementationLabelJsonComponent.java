package io.metadew.iesi.datatypes.dataset.implementation.label;

public class DatasetImplementationLabelJsonComponent {

    public enum Field {
        LABEL_KEY("label");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

}
