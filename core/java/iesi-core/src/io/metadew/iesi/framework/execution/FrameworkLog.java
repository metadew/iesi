package io.metadew.iesi.framework.execution;

import io.metadew.iesi.connection.operation.filetransfer.FileTransfered;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.crypto.RedactionSource;
import io.metadew.iesi.metadata.definition.Context;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class FrameworkLog {

    private FrameworkConfiguration frameworkConfiguration;
    private FrameworkExecutionContext frameworkExecutionContext;
    private Logger logger;
    private String logFile;
    private UUID uuid;
    private FrameworkCrypto frameworkCrypto;
    private ArrayList<String> encryptionRedactionList;
    private ArrayList<RedactionSource> redactionList;

    public FrameworkLog(FrameworkConfiguration frameworkConfiguration, FrameworkExecutionContext frameworkExecutionContext, FrameworkControl frameworkControl, FrameworkCrypto frameworkCrypto) {
        if (frameworkExecutionContext == null) {
            Context context = new Context();
            context.setName("");
            context.setScope("");
            this.setExecutionContext(new FrameworkExecutionContext(context));
        } else {
            this.setExecutionContext(frameworkExecutionContext);
        }

        // Initialize password redaction
        this.setEncryptionRedactionList(new ArrayList<String>());
        this.setRedactionList(new ArrayList<RedactionSource>());

        // Get to work
        this.setFrameworkConfiguration(frameworkConfiguration);
        this.setFrameworkCrypto(frameworkCrypto);
        System.setProperty("log4j.configurationFile",
                this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("conf") + File.separator
                        + this.getFrameworkConfiguration().getFrameworkCode() + "-log4j2-cli.xml");

        // Set log file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        this.setUuid(UUID.randomUUID());
        StringBuilder fileName = new StringBuilder();
        fileName.append(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("logs"));
        fileName.append(File.separator);
        fileName.append(dateFormat.format(new Date()));
        fileName.append((!this.getExecutionContext().getContext().getName().equalsIgnoreCase("") ? "." + this.getExecutionContext().getContext().getName() : ""));
        fileName.append((!this.getExecutionContext().getContext().getScope().equalsIgnoreCase("") ? "." + this.getExecutionContext().getContext().getScope() : ""));
        fileName.append(".");
        fileName.append(this.getUuid().toString());
        fileName.append(".log");
        this.setLogFile(fileName.toString());
        System.setProperty("logFilename", this.getLogFile());

        // Create logger
        this.logger = LogManager.getLogger(this.getFrameworkConfiguration().getFrameworkCode());
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
        String lines[] = message.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String temp = this.getFrameworkCrypto().redact(lines[i]);
            temp = this.getFrameworkCrypto().redact(lines[i], this.getEncryptionRedactionList());

            if (level == Level.TRACE) {
                this.getLogger().trace(temp);
            } else if (level == Level.DEBUG) {
                this.getLogger().debug(temp);
            } else if (level == Level.INFO) {
                this.getLogger().info(temp);
            } else if (level == Level.WARN) {
                this.getLogger().warn(temp);
            } else if (level == Level.ERROR) {
                this.getLogger().error(temp);
            } else if (level == Level.FATAL) {
                this.getLogger().fatal(temp);
            } else {
                this.getLogger().trace(temp);
            }
        }
    }

    public void log(FileTransfered fileTransfered, Level level) {
        String message = "";
        message = "source.path=" + fileTransfered.getSourceFilePath();
        this.log(message, level);
        message = "source.file=" + fileTransfered.getSourceFileName();
        this.log(message, level);
        message = "target.path=" + fileTransfered.getTargetFilePath();
        this.log(message, level);
        message = "target.file=" + fileTransfered.getTargetFileName();
        this.log(message, level);
    }

    // Getters and Setters
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public ArrayList<String> getEncryptionRedactionList() {
        return encryptionRedactionList;
    }

    public void setEncryptionRedactionList(ArrayList<String> encryptionRedactionList) {
        this.encryptionRedactionList = encryptionRedactionList;
    }

    public ArrayList<RedactionSource> getRedactionList() {
        return redactionList;
    }

    public void setRedactionList(ArrayList<RedactionSource> redactionList) {
        this.redactionList = redactionList;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public FrameworkExecutionContext getExecutionContext() {
        return frameworkExecutionContext;
    }

    public void setExecutionContext(FrameworkExecutionContext frameworkExecutionContext) {
        this.frameworkExecutionContext = frameworkExecutionContext;
    }

    public FrameworkCrypto getFrameworkCrypto() {
        return frameworkCrypto;
    }

    public void setFrameworkCrypto(FrameworkCrypto frameworkCrypto) {
        this.frameworkCrypto = frameworkCrypto;
    }

    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }
}