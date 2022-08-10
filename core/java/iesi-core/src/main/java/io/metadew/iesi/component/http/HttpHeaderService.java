package io.metadew.iesi.component.http;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ActionExecution;

import java.text.MessageFormat;

public class HttpHeaderService implements IHttpHeaderService {

    private static HttpHeaderService INSTANCE;
    private final FrameworkCrypto frameworkCrypto = SpringContext.getBean(FrameworkCrypto.class);

    public synchronized static HttpHeaderService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpHeaderService();
        }
        return INSTANCE;
    }

    private HttpHeaderService() {
    }

    public HttpHeader convert(HttpHeaderDefinition httpHeaderDefinition, ActionExecution actionExecution) {
        return new HttpHeader(httpHeaderDefinition.getName(), resolveHeader(httpHeaderDefinition.getValue(), actionExecution));
    }


    private String resolveHeader(String header, ActionExecution actionExecution) {
        String actionResolvedValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(header);
        String resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, actionResolvedValue);
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveConceptLookup(resolvedInputValue).getValue();
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, resolvedInputValue);
        String decryptedInputValue = frameworkCrypto.resolve(resolvedInputValue);
        return convertHeaderDatatype(DataTypeHandler.getInstance().resolve(decryptedInputValue, actionExecution.getExecutionControl().getExecutionRuntime()));
    }

    private String convertHeaderDatatype(DataType header) {
        if (header instanceof Text) {
            return ((Text) header).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("Output http component does not allow header to be of ''{0}''", header.getClass().getSimpleName()));
        }
    }

}
