package io.metadew.iesi.data.generation.output.control;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.data.generation.execution.GenerationControlExecution;
import io.metadew.iesi.data.generation.execution.GenerationControlRuleExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlRule;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.io.*;

public class Header {

    private GenerationControlExecution generationControlExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationControlTypeName = "header";

    // Parameters

    // Constructors
    public Header(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                  GenerationControlExecution generationControlExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationControlExecution(generationControlExecution);
    }

    //
    public boolean execute(String fullFileName) {
        try {
            this.getFrameworkExecution().getFrameworkLog()
                    .log("generation.control.type=" + this.generationControlTypeName, Level.INFO);

            // Reset Parameters

            // Get Parameters

            // Run the generation control
            try {
                StringBuilder header = new StringBuilder();

                for (GenerationControlRule generationControlRule : this.getGenerationControlExecution()
                        .getGenerationControl().getRules()) {
                    GenerationControlRuleExecution generationControlRuleExecution = new GenerationControlRuleExecution(
                            this.getFrameworkExecution(), this.getExecutionControl(),
                            this.getGenerationControlExecution().getGenerationExecution(), generationControlRule);
                    generationControlRuleExecution.execute();
                    header.append(generationControlRuleExecution.getOutput());
                }

                // Create new file
                String tempFullFileName = fullFileName + ".tmp";
                FileTools.delete(tempFullFileName);
                FileTools.appendToFile(tempFullFileName, "",
                        header.toString());

                // Copy file contents
                File file = new File(fullFileName);
                @SuppressWarnings("resource")
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String readLine = "";
                while ((readLine = bufferedReader.readLine()) != null) {
                    FileTools.appendToFile(tempFullFileName, "",
                            readLine);
                }

                // copy file to orginal one
                FileTools.copyFromFileToFile(tempFullFileName, fullFileName);

                // delete temporary file
                FileTools.delete(tempFullFileName);

            } catch (Exception e) {
                throw new RuntimeException("Issue generating header: " + e, e);
            }

            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            // TODO logging

            return false;
        }

    }

    // Getters and Setters
    public String getGenerationControlTypeName() {
        return generationControlTypeName;
    }

    public void setGenerationControlTypeName(String generationControlTypeName) {
        this.generationControlTypeName = generationControlTypeName;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public GenerationControlExecution getGenerationControlExecution() {
        return generationControlExecution;
    }

    public void setGenerationControlExecution(GenerationControlExecution generationControlExecution) {
        this.generationControlExecution = generationControlExecution;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}