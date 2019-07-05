package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.definition.Script;

/**
 * This class contains the condition and destination required for routing purposes.
 *
 * @author peter.billen
 */
public class RouteOperation {

    private int id;
    private ActionParameterOperation condition;
    private ActionParameterOperation destination;
    private Script script;

    public RouteOperation() {

    }

    // Getters and Setters
    public ActionParameterOperation getCondition() {
        return condition;
    }

    public void setCondition(ActionParameterOperation condition) {
        this.condition = condition;
    }

    public ActionParameterOperation getDestination() {
        return destination;
    }

    public void setDestination(ActionParameterOperation destination) {
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }


}