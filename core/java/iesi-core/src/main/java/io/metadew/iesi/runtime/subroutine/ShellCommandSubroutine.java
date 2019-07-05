package io.metadew.iesi.runtime.subroutine;

import io.metadew.iesi.metadata.definition.Subroutine;
import io.metadew.iesi.metadata.definition.SubroutineParameter;

public class ShellCommandSubroutine {

    private Subroutine subroutine;

    // Constructors
    public ShellCommandSubroutine(Subroutine subroutine) {
        this.setSubroutine(subroutine);
    }

    // Methods
    public String getValue() {
        String shellCommand = "";

        // Get Parameters
        for (SubroutineParameter subroutineParameter : this.getSubroutine().getParameters()) {
            if (subroutineParameter.getName().equalsIgnoreCase("command")) {
                shellCommand = subroutineParameter.getValue();
            }
        }
        return shellCommand;
    }

    // Getters and Setters
    public Subroutine getSubroutine() {
        return subroutine;
    }

    public void setSubroutine(Subroutine subroutine) {
        this.subroutine = subroutine;
    }


}