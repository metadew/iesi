package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.InputParameter;
import io.metadew.iesi.metadata.definition.Script;

import java.util.ArrayList;
import java.util.List;

public class ParameterOperation {

    public ParameterOperation() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<InputParameter> getInputParameters(Script script, Action action, ActionParameter actionParameter) {
        List<InputParameter> inputParameterList = new ArrayList();
        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        String replaceValue;
        String input = actionParameter.getValue();
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Replace
            replaceValue = midBit;
            if (replaceValue != null) {
                InputParameter inputParameter = new InputParameter();
                inputParameter.setActionInputParameter(true);
                inputParameter.setName(midBit);
                inputParameter.setScriptName(script.getName());
                inputParameter.setActionName(action.getName());
                inputParameter.setActionParameterName(actionParameter.getName());
                inputParameterList.add(inputParameter);
                input = input.replaceAll(variable_char + midBit + variable_char, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return inputParameterList;
    }

}