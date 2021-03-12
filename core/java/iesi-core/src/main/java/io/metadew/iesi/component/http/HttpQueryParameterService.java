package io.metadew.iesi.component.http;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ActionExecution;

import java.text.MessageFormat;

public class HttpQueryParameterService implements IHttpQueryParameterService {

    private static HttpQueryParameterService INSTANCE;

    public synchronized static HttpQueryParameterService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpQueryParameterService();
        }
        return INSTANCE;
    }

    private HttpQueryParameterService() {
    }


    public HttpQueryParameter convert(HttpQueryParameterDefinition httpQueryParameterDefinition, ActionExecution actionExecution) {
        return new HttpQueryParameter(httpQueryParameterDefinition.getName(), resolveQueryParameter(httpQueryParameterDefinition.getValue(), actionExecution));
    }


    private String resolveQueryParameter(String httpQueryParameter, ActionExecution actionExecution) {
        String actionResolvedValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(httpQueryParameter);
        String resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, actionResolvedValue);
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveConceptLookup(resolvedInputValue).getValue();
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, resolvedInputValue);
        String decryptedInputValue = FrameworkCrypto.getInstance().resolve(resolvedInputValue);
        return convertQueryParameterDatatype(DataTypeHandler.getInstance().resolve(decryptedInputValue, actionExecution.getExecutionControl().getExecutionRuntime()));
    }

    private String convertQueryParameterDatatype(DataType header) {
        if (header instanceof Text) {
            return ((Text) header).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("Output http component does not allow query parameter to be of ''{0}''", header.getClass().getSimpleName()));
        }
    }

}
