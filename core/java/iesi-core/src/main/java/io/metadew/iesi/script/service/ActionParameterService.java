package io.metadew.iesi.script.service;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class ActionParameterService {

    private static ActionParameterService instance;

    public static synchronized ActionParameterService getInstance() {
        if (instance == null) {
            instance = new ActionParameterService();
        }
        return instance;
    }

    private ActionParameterService() {
    }


    public DataType getValue(ActionParameter actionParameter, ExecutionRuntime executionRuntime, ActionExecution actionExecution) {
        if (actionParameter.getValue() == null) {
            return new Null();
        }
        String inputValue = actionParameter.getValue();
        if (actionParameter.getValue() == null) inputValue = "";
        log.debug(String.format("action.param=%s:%s", actionParameter.getMetadataKey().getParameterName(), inputValue));

        // Start manipulation with lookups
        inputValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(inputValue);
        String resolvedInputValue = executionRuntime.resolveVariables(actionExecution, inputValue);

        resolvedInputValue = lookupSubroutine(resolvedInputValue);
        resolvedInputValue = executionRuntime.resolveConceptLookup(resolvedInputValue).getValue();

        // perform lookup again after cross concept lookup
        resolvedInputValue = executionRuntime.resolveVariables(actionExecution, resolvedInputValue);
        log.debug(String.format("action.param.resolved=%s:%s", actionParameter.getMetadataKey().getParameterName(), resolvedInputValue));
        String decryptedInputValue = FrameworkCrypto.getInstance().resolve(resolvedInputValue);

        // Impersonate
//        if (actionTypeParameter.isImpersonate()) {
//            String impersonatedConnectionName = executionControl.getExecutionRuntime()
//                    .getImpersonationOperation().getImpersonatedConnection(decryptedInputValue);
//            if (!impersonatedConnectionName.equalsIgnoreCase("")) {
//                executionControl.logMessage("action." + name
//                        + ".impersonate=" + this.getValue() + ":" + impersonatedConnectionName, Level.DEBUG);
//                resolvedInputValue = impersonatedConnectionName;
//            }
//        }

        // Resolve to data type
        return DataTypeHandler.getInstance().resolve(decryptedInputValue, executionRuntime);
    }

    private String lookupSubroutine(String input) {
        return input;
//        if (actionTypeParameter.getSubroutine() == null || actionTypeParameter.getSubroutine().equalsIgnoreCase(""))
//            return input;
//        SubroutineOperation subroutineOperation = new SubroutineOperation(input);
//        if (subroutineOperation.isValid()) {
//            if (subroutineOperation.getSubroutine().getType().equalsIgnoreCase("query")) {
//                return new SqlStatementSubroutine(subroutineOperation.getSubroutine()).getValue();
//            } else if (subroutineOperation.getSubroutine().getType().equalsIgnoreCase("command")) {
//                return new ShellCommandSubroutine(subroutineOperation.getSubroutine()).getValue();
//            } else {
//                return input;
//            }
//        } else {
//            return input;
//        }
    }

}
