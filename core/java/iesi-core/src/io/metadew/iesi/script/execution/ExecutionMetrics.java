package io.metadew.iesi.script.execution;

/**
 * This class keeps track of all metrics during the execution of a script.
 *
 * @author peter.billen
 */
public class ExecutionMetrics {

    private long successCount;
    private long warningCount;
    private long errorCount;
    private long skipCount;

    // Constructors
    public ExecutionMetrics() {
        this.resetAllMetrics();
    }

    public void mergeExecutionMetrics(ExecutionMetrics executionMetrics) {
        this.setSuccessCount(this.getSuccessCount() + executionMetrics.getSuccessCount());
        this.setWarningCount(this.getWarningCount() + executionMetrics.getWarningCount());
        this.setErrorCount(this.getErrorCount() + executionMetrics.getErrorCount());
        this.setSkipCount(this.getSkipCount() + executionMetrics.getSkipCount());
    }

    // Getters and Setters
    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public void increaseSuccessCount(long increase) {
        this.successCount = this.successCount + increase;
    }

    public long getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(long warningCount) {
        this.warningCount = warningCount;
    }

    public void increaseWarningCount(long increase) {
        this.warningCount = this.warningCount + increase;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public void increaseErrorCount(long increase) {
        this.errorCount = this.errorCount + increase;
    }

    public void resetAllMetrics() {
        this.setSuccessCount(0);
        this.setWarningCount(0);
        this.setErrorCount(0);
        this.setSkipCount(0);
    }

    public void resetErrorCount() {
        this.setErrorCount(0);
    }

    public void resetWarningCount() {
        this.setWarningCount(0);
    }

    public void resetSuccessCount() {
        this.setSuccessCount(0);
    }

    public void resetSkipCount() {
        this.setSkipCount(0);
    }

    public long getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(long skipCount) {
        this.skipCount = skipCount;
    }

    public void increaseSkipCount(long increase) {
        this.skipCount = this.skipCount + increase;
    }

}