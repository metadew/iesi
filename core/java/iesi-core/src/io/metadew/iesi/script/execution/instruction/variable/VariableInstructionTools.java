package io.metadew.iesi.script.execution.instruction.variable;

public final class VariableInstructionTools {

    public static String getSynonymKey(String input) {
        String output = "";
        switch (input) {
            case "fwk.home":
            case "iesi.home":
                output = "fwk.home";
                break;
            case "fwk.version":
            case "fwk.v":
                output = "fwk.version";
                break;
            case "run.env":
            case "run.environment":
                output = "run.env";
                break;
            case "run.id":
            case "run.identifier":
                output = "run.id";
                break;
            default:
                output = input;
                break;
        }
        return output;
    }

}