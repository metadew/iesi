package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import java.text.MessageFormat;

public class TextReplace implements DataInstruction {

    @Override
    public String generateOutput(String parameters) {

        String [] args = parameters.split(",");
        if(args.length == 3){
            String text = args[0];
            String first = args[1];
            String end = args[2];
            text = text.replaceAll(first,end);
            return text;
        }else if (args.length==2) {
            String text = args[0];
            String first = args[1];
            text = text.replaceAll(first, "");
            return text;
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        }
    }

    @Override
    public String getKeyword() { return "text.replace"; }

}
