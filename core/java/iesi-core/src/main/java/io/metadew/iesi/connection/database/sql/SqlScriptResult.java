package io.metadew.iesi.connection.database.sql;

/**
 * Object containing the result of a SQL script execution on a database.
 *
 * @author peter.billen
 */
public class SqlScriptResult {

    private int returnCode;
    private String systemOutput;
    private String errorOutput;

    public SqlScriptResult(int returnCode, String systemOutput, String errorOutput) {
        this.setReturnCode(returnCode);
        this.setSystemOutput(systemOutput);
        this.setErrorOutput(errorOutput);
    }

    // Getters and Setters
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public int getReturnCode() {
        return returnCode;
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


}
