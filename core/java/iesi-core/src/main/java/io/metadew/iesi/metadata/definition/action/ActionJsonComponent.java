package io.metadew.iesi.metadata.definition.action;

public class ActionJsonComponent {

    public enum Field {
        ID_KEY("id"),
        NUMBER_KEY("number"),
        TYPE_KEY("type"),
        NAME_KEY("name"),
        DESCRIPTION_KEY("description"),
        COMPONENT_KEY("component"),
        CONDITION_KEY("condition"),
        ITERATION_KEY("iteration"),
        ERROR_EXPECTED_KEY("errorExpected"),
        ERROR_STOP_KEY("errorStop"),
        RETRIES_KEY("retries"),
        PARAMETERS_KEY("parameters");


        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

}
