//package io.metadew.iesi.script.action.conn;
//
//import io.metadew.iesi.datatypes.DataType;
//import io.metadew.iesi.datatypes.text.Text;
//import io.metadew.iesi.metadata.definition.action.ActionParameter;
//import io.metadew.iesi.script.action.ActionTypeExecution;
//import io.metadew.iesi.script.execution.ActionExecution;
//import io.metadew.iesi.script.execution.ExecutionControl;
//import io.metadew.iesi.script.execution.ScriptExecution;
//import io.metadew.iesi.script.operation.ActionParameterOperation;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.text.MessageFormat;
//
//public class ConnSetStageConnection extends ActionTypeExecution {
//
//    // Parameters
//    private ActionParameterOperation stageName;
//    private ActionParameterOperation stageCleanup;
//    private static final Logger LOGGER = LogManager.getLogger();
//
//    public ConnSetStageConnection(ExecutionControl executionControl,
//                                  ScriptExecution scriptExecution, ActionExecution actionExecution) {
//        super(executionControl, scriptExecution, actionExecution);
//    }
//
//    public void prepare() {
//        // Reset Parameters
//        this.setStageName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
//                this.getActionExecution().getAction().getType(), "stage"));
//        this.setStageCleanup(new ActionParameterOperation(this.getExecutionControl(),
//                this.getActionExecution(), this.getActionExecution().getAction().getType(), "cleanUp"));
//
//        // Get Parameters
//        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
//            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("stage")) {
//                this.getStageName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
//            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("cleanup")) {
//                this.getStageCleanup().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
//            }
//        }
//
//        // Create parameter list
//        this.getActionParameterOperationMap().put("stage", this.getStageName());
//        this.getActionParameterOperationMap().put("cleanup", this.getStageCleanup());
//    }
//
//    protected boolean executeAction() throws InterruptedException {
//        // Set the stage connection
//        String stageName = convertStageName(getStageName().getValue());
//        boolean cleanup = convertCleanup(getStageCleanup().getValue());
//        this.getExecutionControl().getExecutionRuntime().setStage(stageName, cleanup);
//
//        // Increase the success count
//        this.getActionExecution().getActionControl().increaseSuccessCount();
//        return true;
//    }
//
//    @Override
//    protected String getKeyword() {
//        return "conn.setStageConnection";
//    }
//
//    private String convertStageName(DataType stageName) {
//        if (stageName instanceof Text) {
//            return stageName.toString();
//        } else {
//            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for stage name",
//                    stageName.getClass()));
//            return stageName.toString();
//        }
//    }
//
//    private boolean convertCleanup(DataType cleanup) {
//        // TODO: make optional
//        if (cleanup == null) {
//            return false;
//        }
//        if (cleanup instanceof Text) {
//            return cleanup.toString().equalsIgnoreCase("y");
//        } else {
//            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for cleanup",
//                    cleanup.getClass()));
//            return false;
//        }
//    }
//
//    public ActionParameterOperation getStageName() {
//        return stageName;
//    }
//
//    public void setStageName(ActionParameterOperation stageName) {
//        this.stageName = stageName;
//    }
//
//    public ActionParameterOperation getStageCleanup() {
//        return stageCleanup;
//    }
//
//    public void setStageCleanup(ActionParameterOperation stageCleanup) {
//        this.stageCleanup = stageCleanup;
//    }
//
//}