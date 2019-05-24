package io.metadew.iesi.connection.host;

/**
 * Object containing result of a shell command execution.
 *
 * @author peter.billen
 */
public class ShellCommandResult {

    private int returnCode;
    private String systemOutput;
    private String errorOutput;
    private String runtimeVariablesOutput;

    public ShellCommandResult(int returnCode, String systemOutput, String errorOutput) {
        this.setReturnCode(returnCode);
        this.setSystemOutput(systemOutput);
        this.setErrorOutput(errorOutput);
        this.setRuntimeVariablesOutput("");
    }

    public ShellCommandResult(int returnCode, String systemOutput, String errorOutput, String runtimeVariablesOutput) {
        this.setReturnCode(returnCode);
        this.setSystemOutput(systemOutput);
        this.setErrorOutput(errorOutput);
        this.setRuntimeVariablesOutput(runtimeVariablesOutput);
    }

    public int getReturnCode() {
        return returnCode;
    }

    // Getters and Setters
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getSystemOutput() {
        return systemOutput;
    }

    public void setSystemOutput(String systemOutput) {
        this.systemOutput = systemOutput;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public void setErrorOutput(String errorOutput) {
        this.errorOutput = errorOutput;
    }

    public String getRuntimeVariablesOutput() {
        return runtimeVariablesOutput;
    }

    public void setRuntimeVariablesOutput(String runtimeVariablesOutput) {
        this.runtimeVariablesOutput = runtimeVariablesOutput;
    }


}
