package io.metadew.iesi.script.action.wfa;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Scanner;

public class WfaGetConfirmation extends ActionTypeExecution {

    // Parameters
    private static final String TYPE_KEY = "type";
    private static final String QUESTION_KEY = "question";
    private static final String TIMEOUT_KEY = "timeout";
    private final int defaultTimeoutInterval = 1000;
    private static final Logger LOGGER = LogManager.getLogger();


    public WfaGetConfirmation(ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
    }


    protected boolean executeAction() throws InterruptedException {
        int timeoutInterval = convertTimeoutInterval(getParameterResolvedValue(TIMEOUT_KEY));
        String question = convertQuestion(getParameterResolvedValue(QUESTION_KEY));
        String type = convertType(getParameterResolvedValue(TYPE_KEY));

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
            getActionExecution().getActionControl().logOutput("action.error",
                    "Confirmation was negative");
            this.getActionExecution().getActionControl().increaseErrorCount();
        }

        return true;
    }

    @Override
    protected String getKeyword() {
        return "wfa.getConfirmation";
    }

    private String convertType(DataType type) {
        if (type instanceof Text) {
            return type.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for type",
                    type.getClass()));
            return type.toString();
        }
    }

    private String convertQuestion(DataType question) {
        if (question instanceof Text) {
            return question.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for question",
                    question.getClass()));
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
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for timeout interval",
                    timeoutInterval.getClass()));
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
            prompt = question + " Y/[N]/STOP ";
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
            prompt = question + " [Y]/STOP ";
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
            prompt = "AUTO-CONFIRMATION: " + question;
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

}