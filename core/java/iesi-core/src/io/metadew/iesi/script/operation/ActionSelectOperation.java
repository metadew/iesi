package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.definition.Action;

import java.util.ArrayList;

/**
 * Operation to manage selecting specific actions to be include or excluded from execution
 *
 * @author peter.billen
 */
public class ActionSelectOperation {

    private String type = "number";
    private String mode = "include";
    private ArrayList<String> fromList = null;
    private ArrayList<String> toList = null;
    private boolean active = true;

    // Constructors
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ActionSelectOperation(String input) {
        // Create arraylists
        this.setFromList(new ArrayList());
        this.setToList(new ArrayList());

        // Read input
        this.loadInput(input);
    }

    // Methods
    private void loadInput(String input) {
        String[] parts = input.split(",");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            int delim = innerpart.indexOf("=");
            if (delim > 0) {
                String key = innerpart.substring(0, delim);
                String value = innerpart.substring(delim + 1);

                if (key.equalsIgnoreCase("mode")) {
                    this.setMode(value);
                    if (this.getMode().equalsIgnoreCase("include")) {
                        this.setActive(false);
                    } else if (this.getMode().equalsIgnoreCase("exclude")) {
                        this.setActive(true);
                    }
                }

                if (key.equalsIgnoreCase("type")) {
                    this.setType(value);
                }

                if (key.equalsIgnoreCase("scope")) {
                    this.loadActions(value);
                }
            } else {
                // Not a valid configuration
            }
        }
    }

    private void loadActions(String actions) {
        String[] parts = actions.split(";");
        for (int i = 0; i < parts.length; i++) {
            String innerpart = parts[i];
            int delim = innerpart.indexOf("-");
            if (delim > 0) {
                String from = innerpart.substring(0, delim);
                String to = innerpart.substring(delim + 1);

                this.getFromList().add(from);
                this.getToList().add(to);
            } else {
                this.getFromList().add(innerpart);
                this.getToList().add(innerpart);
            }
        }
    }

    public boolean getExecutionStatus(Action action) {
        if (this.getMode().equalsIgnoreCase("include")) {
            if (inList(this.getFromList(), Long.toString(action.getNumber()))) {
                if (!this.isActive()) {
                    this.setActive(true);
                }
                return true;
            } else {
                return this.isActive();
            }
        } else if (this.getMode().equalsIgnoreCase("exclude")) {
            if (inList(this.getFromList(), Long.toString(action.getNumber()))) {
                if (this.isActive()) {
                    this.setActive(false);
                }
                return false;
            } else {
                return this.isActive();
            }
        } else {
            throw new RuntimeException("Invalid action selection mode");
        }
    }

    public void setContinueStatus(Action action) {
        if (this.getMode().equalsIgnoreCase("include")) {
            if (inList(this.getToList(), Long.toString(action.getNumber()))) {
                if (this.isActive()) {
                    this.setActive(false);
                }
            }
        } else if (this.getMode().equalsIgnoreCase("exclude")) {
            if (inList(this.getToList(), Long.toString(action.getNumber()))) {
                if (!this.isActive()) {
                    this.setActive(true);
                }
            }
        } else {
            throw new RuntimeException("Invalid action selection mode");
        }
    }

    private boolean inList(ArrayList<String> list, String checkItem) {
        boolean tempResult = false;

        for (String curVal : list) {
            if (curVal.equalsIgnoreCase(checkItem)) {
                tempResult = true;
            }
        }

        return tempResult;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getFromList() {
        return fromList;
    }

    public void setFromList(ArrayList<String> fromList) {
        this.fromList = fromList;
    }

    public ArrayList<String> getToList() {
        return toList;
    }

    public void setToList(ArrayList<String> toList) {
        this.toList = toList;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}