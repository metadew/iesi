package io.metadew.iesi.framework.execution;

import io.metadew.iesi.connection.operation.filetransfer.FileTransfered;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import org.apache.logging.log4j.*;

import java.util.ArrayList;

public class FrameworkLog {

    private Logger frameworkLogger;
    private ArrayList<String> encryptionRedactionList;

    private static FrameworkLog INSTANCE;
    private static final Marker FWK = MarkerManager.getMarker("FWK");

    public static FrameworkLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkLog();
        }
        return INSTANCE;
    }

    private FrameworkLog() {}

    public void init() {
        this.encryptionRedactionList = new ArrayList<>();
        this.frameworkLogger = LogManager.getLogger(getClass());
    }

    public void log(String message, Level level) {
        log(FWK, message, level);
    }

    public void log(Marker marker, String message, Level level) {
        String[] lines = message.split("\\r?\\n");
        for (String line : lines) {
            line = FrameworkCrypto.getInstance().redact(line);
            line = FrameworkCrypto.getInstance().redact(line, encryptionRedactionList);
            frameworkLogger.log(level, marker, line);
        }
    }

    public void log(FileTransfered fileTransfered, Level level) {
        this.log("source.path=" + fileTransfered.getSourceFilePath(), level);
        this.log("source.file=" + fileTransfered.getSourceFileName(), level);
        this.log("target.path=" + fileTransfered.getTargetFilePath(), level);
        this.log("target.file=" + fileTransfered.getTargetFileName(), level);
    }

    public ArrayList<String> getEncryptionRedactionList() {
        return encryptionRedactionList;
    }

}