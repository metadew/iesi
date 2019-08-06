package io.metadew.iesi.framework.execution;

import io.metadew.iesi.connection.operation.filetransfer.FileTransfered;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.crypto.RedactionSource;
import io.metadew.iesi.metadata.definition.Context;
import org.apache.logging.log4j.*;

import java.util.ArrayList;

public class FrameworkLog {

    private Logger frameworkLogger;
    private FrameworkCrypto frameworkCrypto;
    private ArrayList<String> encryptionRedactionList;
    private ArrayList<RedactionSource> redactionList;

    private static FrameworkLog INSTANCE;
    private static final Marker FWK = MarkerManager.getMarker("FWK");

    public static FrameworkLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkLog();
        }
        return INSTANCE;
    }

    private FrameworkLog() {}

    public void init(FrameworkConfiguration frameworkConfiguration, FrameworkExecutionContext frameworkExecutionContext,
                        FrameworkControl frameworkControl, FrameworkCrypto frameworkCrypto, FrameworkRuntime frameworkRuntime) {
        if (frameworkExecutionContext == null) {
            frameworkExecutionContext = new FrameworkExecutionContext(new Context("", ""));
        }
        // Initialize password redaction
        this.encryptionRedactionList = new ArrayList<>();
        this.redactionList = new ArrayList<>();
        this.frameworkCrypto = frameworkCrypto;

        // TODO:
        //System.setProperty("log4j.configurationFile",
        //        frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("conf") + File.separator
        // + frameworkConfiguration.getFrameworkCode() + "-log4j2-cli.xml");
//        ThreadContext.put("fwk.code", frameworkConfiguration.getFrameworkCode());
//        ThreadContext.put("location", frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("logs"));
//        ThreadContext.put("context.name", frameworkExecutionContext.getContext().getName());
//        ThreadContext.put("context.scope", frameworkExecutionContext.getContext().getScope());
//        ThreadContext.put("fwk.runid", frameworkRuntime.getRunId());


        // Set log file name
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        StringBuilder fileName = new StringBuilder();
//        fileName.append(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("logs"));
//        fileName.append(File.separator);
//        fileName.append(dateFormat.format(new Date()));
//        fileName.append((!this.getExecutionContext().getContext().getName().equalsIgnoreCase("") ? "." + this.getExecutionContext().getContext().getName() : ""));
//        fileName.append((!this.getExecutionContext().getContext().getScope().equalsIgnoreCase("") ? "." + this.getExecutionContext().getContext().getScope() : ""));
//        fileName.append(".");
//        fileName.append(this.getFrameworkRuntime().getRunId());
//        fileName.append(".log");
//        this.setLogFile(fileName.toString());
//        System.setProperty("logFilename", this.getLogFile());

        // Create frameworkLogger
        this.frameworkLogger = LogManager.getLogger(getClass());
    }

    // Methods
    /*
     * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF. ALL All levels including
     * custom levels. TRACE Designates finer-grained informational events than
     * the DEBUG. DEBUG Designates fine-grained informational events that are
     * most useful to debug an application. INFO Designates informational
     * messages that highlight the progress of the application at coarse-grained
     * level. WARN Designates potentially harmful situations. ERROR Designates
     * error events that might still allow the application to continue running.
     * FATAL Designates very severe error events that will presumably lead the
     * application to abort. OFF The highest possible rank and is intended to
     * turn off logging.
     */
    public void log(String message, Level level) {
        log(FWK, message, level);
    }

    public void log(Marker marker, String message, Level level) {
        String[] lines = message.split("\\r?\\n");
        for (String line : lines) {
            line = frameworkCrypto.redact(line);
            line = frameworkCrypto.redact(line, encryptionRedactionList);
            frameworkLogger.log(level, marker, line);
        }
    }

    // TODO: look at Logger Messages
    public String prepareLog(String message) {
        return frameworkCrypto.redact(frameworkCrypto.redact(message), encryptionRedactionList);
    }

    public String[] prepareLog(FileTransfered fileTransfered) {
        return new String[] {
                "source.path=" + fileTransfered.getSourceFilePath(),
                "source.file=" + fileTransfered.getSourceFileName(),
                "target.path=" + fileTransfered.getTargetFilePath(),
                "target.file=" + fileTransfered.getTargetFileName() };
    }

    public void log(FileTransfered fileTransfered, Level level) {
        this.log("source.path=" + fileTransfered.getSourceFilePath(), level);
        this.log("source.file=" + fileTransfered.getSourceFileName(), level);
        this.log("target.path=" + fileTransfered.getTargetFilePath(), level);
        this.log("target.file=" + fileTransfered.getTargetFileName(), level);
    }

    // Getters and Setters
    public Logger getFrameworkLogger() {
        return frameworkLogger;
    }

    public void setFrameworkLogger(Logger frameworkLogger) {
        this.frameworkLogger = frameworkLogger;
    }

    public ArrayList<String> getEncryptionRedactionList() {
        return encryptionRedactionList;
    }

}