package io.metadew.iesi.assembly;

import io.metadew.iesi.assembly.operation.FileSystemOperation;
import io.metadew.iesi.connection.tools.OutputTools;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkLog;

public class AssemblyContext {

    private FrameworkControl frameworkControl;
    private FrameworkCrypto frameworkCrypto;
    private FrameworkLog frameworkLog;

    private OutputTools outputTools;
    private FileSystemOperation fileSystemOperation;

    private String repositoryHome;

    public AssemblyContext(String repositoryHome) {
        this.setRepositoryHome(repositoryHome);
        this.createInstallationTools();
    }

    // Methods
    private void createInstallationTools() {
        this.setFrameworkCrypto(new FrameworkCrypto());
        this.setConfigTools(new FrameworkControl(new FrameworkConfiguration(this.getRepositoryHome()), "assembly", this.getRepositoryHome()));

        // Create other tools
        this.setOutputTools(new OutputTools());
        this.setFileSystemOperation(new FileSystemOperation());

    }

    // Getters and Setters
    public FrameworkControl getConfigTools() {
        return frameworkControl;
    }

    public void setConfigTools(FrameworkControl frameworkControl) {
        this.frameworkControl = frameworkControl;
    }

    public FrameworkLog getLoggingTools() {
        return frameworkLog;
    }

    public void setLoggingTools(FrameworkLog frameworkLog) {
        this.frameworkLog = frameworkLog;
    }

    public OutputTools getOutputTools() {
        return outputTools;
    }

    public void setOutputTools(OutputTools outputTools) {
        this.outputTools = outputTools;
    }

    public FileSystemOperation getFileSystemOperation() {
        return fileSystemOperation;
    }

    public void setFileSystemOperation(FileSystemOperation fileSystemOperation) {
        this.fileSystemOperation = fileSystemOperation;
    }

    public String getRepositoryHome() {
        return repositoryHome;
    }

    public void setRepositoryHome(String repositoryHome) {
        this.repositoryHome = repositoryHome;
    }

    public FrameworkCrypto getFrameworkCrypto() {
        return frameworkCrypto;
    }

    public void setFrameworkCrypto(FrameworkCrypto frameworkCrypto) {
        this.frameworkCrypto = frameworkCrypto;
    }

}