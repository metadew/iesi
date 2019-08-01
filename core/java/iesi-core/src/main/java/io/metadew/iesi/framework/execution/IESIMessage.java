package io.metadew.iesi.framework.execution;

import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import org.apache.logging.log4j.message.Message;

public class IESIMessage implements Message {

    private final String message;

    public IESIMessage(String message) {
        this.message = message;
    }

    @Override
    public String getFormattedMessage() {
        return FrameworkCrypto.getInstance().redact(message);
    }

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }
}
