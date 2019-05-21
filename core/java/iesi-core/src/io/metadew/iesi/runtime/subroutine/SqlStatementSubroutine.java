package io.metadew.iesi.runtime.subroutine;

import io.metadew.iesi.metadata.definition.Subroutine;
import io.metadew.iesi.metadata.definition.SubroutineParameter;

public class SqlStatementSubroutine {

    private Subroutine subroutine;

    // Constructors
    public SqlStatementSubroutine(Subroutine subroutine) {
        this.setSubroutine(subroutine);
    }

    // Methods
    public String getValue() {
        String sqlStatement = "";

        // Get Parameters
        for (SubroutineParameter subroutineParameter : this.getSubroutine().getParameters()) {
            if (subroutineParameter.getName().equalsIgnoreCase("query")) {
                sqlStatement = subroutineParameter.getValue();
            }
        }
        return sqlStatement;
    }

    // Getters and Setters
    public Subroutine getSubroutine() {
        return subroutine;
    }

    public void setSubroutine(Subroutine subroutine) {
        this.subroutine = subroutine;
    }


}