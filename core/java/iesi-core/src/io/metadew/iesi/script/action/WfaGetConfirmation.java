package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Scanner;

public class WfaGetConfirmation {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation confirmationType;
    private ActionParameterOperation confirmationQuestion;
    private ActionParameterOperation timeoutInterval;
    private final int defaultTimeoutInterval = 1000;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public WfaGetConfirmation() {

    }

    public WfaGetConfirmation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Set Parameters
        this.setConfirmationType(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "type"));
        this.setConfirmationQuestion(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "question"));
        this.setTimeoutInterval(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "timeout"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("type")) {
                this.getConfirmationType().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("question")) {
                this.getConfirmationQuestion().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("timeout")) {
                this.getTimeoutInterval().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("type", this.getConfirmationType());
        this.getActionParameterOperationMap().put("question", this.getConfirmationQuestion());
        this.getActionParameterOperationMap().put("timeout", this.getTimeoutInterval());
    }

    @SuppressWarnings("unused")
	public boolean execute() {
        try {
            int timeoutInterval = convertTimeoutInterval(getTimeoutInterval().getValue());
            String question = convertQuestion(getConfirmationQuestion().getValue());
            String type = convertType(getConfirmationType().getValue());

            // Run the action
            boolean result = false;
            switch (type.toLowerCase()) {
                case "y":
                    result = this.getConfirmationYes(question);
                    break;
                case "y/n":
                    result = this.getConfirmationYesNo(question);
                    break;
                case "auto":
                    result = this.getConfirmationAuto(question);
                    break;
            }

            // Evaluate result
            if (result) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
            } else {
                this.getActionExecution().getActionControl().increaseErrorCount();
            }

            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private String convertType(DataType type) {
        if (type instanceof Text) {
            return type.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for type",
                    type.getClass()), Level.WARN);
            return type.toString();
        }
    }

    private String convertQuestion(DataType question) {
        if (question instanceof Text) {
            return question.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for question",
                    question.getClass()), Level.WARN);
            return question.toString();
        }
    }


    private int convertTimeoutInterval(DataType timeoutInterval) {
        if (timeoutInterval == null) {
            return defaultTimeoutInterval;
        }
        if (timeoutInterval instanceof Text) {
            return Integer.parseInt(timeoutInterval.toString());
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for timeout interval",
                    timeoutInterval.getClass()), Level.WARN);
            return defaultTimeoutInterval;
        }
    }

    private boolean getConfirmationYesNo(String question) {
        // create a scanner so we can read the command-line input
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        // prompt
        String prompt = null;
        if (question != null && !question.isEmpty()) {
            prompt = this.getConfirmationQuestion().getValue() + " Y/[N]/STOP ";
        } else {
            prompt = "Has the action been finished successfully? Y/[N]/STOP ";
        }
        System.out.print(prompt);

        // Get Input
        boolean getInput = false;
        String readInput = null;
        while (!getInput) {
            readInput = null;
            if ((readInput = scanner.nextLine()).isEmpty()) {
                readInput = "N";
                getInput = true;
            }

            if (!getInput) {
                if (readInput.equalsIgnoreCase("Y") || readInput.equalsIgnoreCase("N")
                        || readInput.toUpperCase().equalsIgnoreCase("STOP")) {
                    getInput = true;
                } else {
                    System.out.print(prompt);
                }
            }
        }

        // Log result
        readInput = readInput.toUpperCase();
        String userComment = "";
        if (readInput.equalsIgnoreCase("N"))
            userComment = this.getConfirmationComment();
        this.getActionExecution().getActionControl().logOutput("confirmation", readInput);

        if (readInput.equalsIgnoreCase("N"))
            this.getActionExecution().getActionControl().logOutput("comment", userComment);

        // Stopping process on user request
        if (readInput.equalsIgnoreCase("STOP")) {
            this.getActionExecution().getAction().setErrorStop("Y");
            return false;
        } else if (readInput.equalsIgnoreCase("N")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean getConfirmationYes(String question) {
        // create a scanner so we can read the command-line input
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        // prompt
        String prompt;
        if (question != null && !question.isEmpty()) {
            prompt = this.getConfirmationQuestion().getValue() + " [Y]/STOP ";
        } else {
            prompt = "Do you confirm to proceed? [Y]/STOP ";
        }
        System.out.print(prompt);

        // Get Input
        boolean getInput = false;
        String readInput = null;
        while (!getInput) {
            readInput = null;
            if ((readInput = scanner.nextLine()).isEmpty()) {
                readInput = "Y";
                getInput = true;
            }

            if (!getInput) {
                if (readInput.equalsIgnoreCase("y") || readInput.equalsIgnoreCase("stop")) {
                    getInput = true;
                } else {
                    System.out.print(prompt);
                }
            }
        }

        // Log result
        readInput = readInput.toUpperCase();
        this.getActionExecution().getActionControl().logOutput("confirmation", readInput);

        // Stopping process on user request
        if (readInput.equalsIgnoreCase("STOP")) {
            this.getActionExecution().getAction().setErrorStop("Y");
            return false;
        } else {
            return true;
        }
    }

    public String getConfirmationComment() {
        // create a scanner so we can read the command-line input
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        // prompt
        String prompt = null;
        prompt = "Please enter a comment: ";
        System.out.print(prompt);

        // Get Input
        boolean getInput = false;
        String readInput = null;
        while (!getInput) {
            readInput = null;
            readInput = scanner.nextLine();

            if (!getInput) {
                if (readInput.length() > 0) {
                    getInput = true;
                } else {
                    System.out.print(prompt);
                }
            }
        }

        return readInput;
    }

    private boolean getConfirmationAuto(String question) {
        // prompt
        String prompt = null;
        if (question != null && !question.isEmpty()) {
            prompt = "AUTO-CONFIRMATION: " + this.getConfirmationQuestion().getValue();
        } else {
            prompt = "AUTO-CONFIRMATION: The action has been confirmed automatically!";
        }
        System.out.print(prompt);
        System.out.println("");

        String readInput = "Y";

        // Log result
        readInput = readInput.toUpperCase();
        this.getActionExecution().getActionControl().logOutput("confirmation", readInput);
        return true;
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getActionParameterOperation(String key) {
        return this.getActionParameterOperationMap().get(key);
    }

    public ActionParameterOperation getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ActionParameterOperation confirmationType) {
        this.confirmationType = confirmationType;
    }

    public ActionParameterOperation getConfirmationQuestion() {
        return confirmationQuestion;
    }

    public void setConfirmationQuestion(ActionParameterOperation confirmationQuestion) {
        this.confirmationQuestion = confirmationQuestion;
    }

    public ActionParameterOperation getTimeoutInterval() {
        return timeoutInterval;
    }

    public void setTimeoutInterval(ActionParameterOperation timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

}