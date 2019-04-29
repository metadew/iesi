package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.connection.tools.SQLTools;

public class FileLookup implements LookupInstruction {

    @Override
    public String getKeyword() {
        return "file";
    }

    @Override
    public String generateOutput(String parameters) {
        return SQLTools.getFirstSQLStmt(parameters.trim());
    }
}
