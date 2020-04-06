package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.configuration.subroutine.SubroutineConfiguration;
import io.metadew.iesi.metadata.definition.subroutine.Subroutine;

import java.util.ArrayList;

/**
 * Operation to manage subroutines during script execution.
 *
 * @author peter.billen
 */
public class SubroutineOperation {

    private String input;
    private boolean valid = false;
    private String type;
    private String name;
    private Subroutine subroutine;
    private ArrayList<String> subroutineTypeList = null;

    // Constructors
    public SubroutineOperation(String input) {
        this.initializeSubroutineTypeList();
        this.setInput(input);
    }

    // Methods
    private void resolveFunction() {

        if (input == null) input = "";
        input = input.trim();

        String typeChar = "=";
        if (input.startsWith(typeChar)) {
            // Check last character
            if (!input.substring(input.length() - 1).equalsIgnoreCase(")")) {
                throw new RuntimeException("Incorrect parameter syntax for: " + input);
            }

            // Get type
            int openPos;
            int closePos;
            String startTypeChar = "(";
            String type;
            String temp = input;
            if (input.indexOf(startTypeChar) > 0) {
                openPos = temp.indexOf(typeChar);
                closePos = temp.indexOf(startTypeChar, openPos + 1);
                type = temp.substring(openPos + 1, closePos);
            } else {
                throw new RuntimeException("Incorrect parameter syntax for: " + input);
            }

            // Get Type Value
            String name = input.substring(closePos + 1, input.length() - 1);

            this.setType(type);
            this.setName(name);
            this.setValid(true);
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initializeSubroutineTypeList() {
        this.setSubroutineTypeList(new ArrayList());
        // get from database + how to integrate regex
        this.getSubroutineTypeList().add("srt");
    }

    private void validateType() {
        // ListTools.inList(this.getSubroutineTypeList(), this.getType()
        if (!this.getSubroutineTypeList().contains(this.getType())) {
            throw new RuntimeException("Invalid subrouting type: " + this.getType());
        }
    }

    public void setInput(String input) {
        this.input = input;
        this.resolveFunction();
        ;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.validateType();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        if (valid) {
            SubroutineConfiguration subroutineConfiguration = new SubroutineConfiguration();
            this.setSubroutine(subroutineConfiguration.getSubroutine(this.getName()));

            if (this.getSubroutine().getName() == null) {
                throw new RuntimeException("No subrountine function found: " + this.getName());
            }

        } else {
            this.setSubroutine(null);
        }
        this.valid = valid;
    }

    public Subroutine getSubroutine() {
        return subroutine;
    }

    public void setSubroutine(Subroutine subroutine) {
        this.subroutine = subroutine;
    }

    public ArrayList<String> getSubroutineTypeList() {
        return subroutineTypeList;
    }

    public void setSubroutineTypeList(ArrayList<String> subroutineTypeList) {
        this.subroutineTypeList = subroutineTypeList;
    }


}