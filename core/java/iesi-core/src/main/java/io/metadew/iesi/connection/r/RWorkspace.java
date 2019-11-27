package io.metadew.iesi.connection.r;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RWorkspace {

    private static final Logger LOGGER = LogManager.getLogger();
    private Path workspacePath;
    private List<String> workspacePreparationScripts;

    public RWorkspace(Path workspacePath) {
        this.workspacePath = workspacePath;
    }

    public RWorkspace(String workspacePath) throws IOException {
        if (!Files.exists(Paths.get(workspacePath))) {
            Files.createDirectory(Paths.get(workspacePath));
        } else if (!Files.isDirectory(Paths.get(workspacePath))) {
            throw new RuntimeException("Cannot create R workspace, provided path is a file");
        }
        this.workspacePath = Paths.get(workspacePath);
        workspacePreparationScripts = new ArrayList<>();
    }

    public RCommandResult addPreparationScript(String preparationScript) throws IOException, InterruptedException {
        workspacePreparationScripts.add(preparationScript);
        return addPreparationScriptToHistory(preparationScript);
    }

    private RCommandResult addPreparationScriptToHistory(String preparationScript) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(workspacePath.toFile());
        processBuilder.command("Rscript", "--restore", "--save", "-e", createPreparationScriptCommand(preparationScript));
        LOGGER.debug("executing: " + String.join(" ", processBuilder.command()));
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        int errorCode = process.waitFor();
        return new RCommandResult(errorCode, reader);
    }

    public List<RCommandResult> removePreparationScript(String preparationScript) throws IOException, InterruptedException {
        List<RCommandResult> rCommandResults = new ArrayList<>();
        workspacePreparationScripts.remove(preparationScript);
        cleanWorkspace();
        for (String workspacePreparationScript : workspacePreparationScripts) {
            rCommandResults.add(addPreparationScriptToHistory(workspacePreparationScript));
        }
        return rCommandResults;
    }


    public void cleanPreparationScripts(String preparationScript) throws IOException {
        workspacePreparationScripts.clear();
        cleanWorkspace();
    }

    public void cleanWorkspace() throws IOException {
        if (Files.exists(workspacePath.resolve(".RData"))) {
            Files.delete(workspacePath.resolve(".RData"));
        }
        if (Files.exists(workspacePath.resolve(".Rhistory"))) {
            Files.delete(workspacePath.resolve(".Rhistory"));
        }
    }

    public RCommandResult executeScript(String script) throws IOException, InterruptedException {
        return executeScript(script, true);
    }

    public RCommandResult executeScript(String script, boolean waitFor) throws IOException, InterruptedException {
        return executeScript(script, waitFor, false);
    }

    public RCommandResult executeScript(String script, boolean waitFor, boolean saveToWorkspace) throws IOException, InterruptedException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        CommandLine commandline = CommandLine.parse("Rscript --verbose --restore --save " + script + " > outputFile.Rout 2>&1");
//        DefaultExecutor exec = new DefaultExecutor();
//        exec.setWorkingDirectory(workspacePath.toFile());
//        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
//        exec.setStreamHandler(streamHandler);
//        int statusCode = exec.execute(commandline);
//        return new RCommandResult(statusCode, outputStream.toString());

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(workspacePath.toFile());
        if (saveToWorkspace) {
            processBuilder.command("Rscript", "--restore", "--save", script);
        } else {
            processBuilder.command("Rscript", "--restore", script);
        }
        LOGGER.debug("executing: " + String.join(" ", processBuilder.command()));
        Process process = processBuilder.start();
        if (waitFor) {
            int errorCode = process.waitFor();
            processBuilder.redirectErrorStream(true);
            File fileName = workspacePath.resolve("output_script.txt").toFile();
            processBuilder.redirectOutput(fileName);
            Scanner s = new Scanner(process.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (s.hasNextLine()) {
                builder.append(s.nextLine());
            }
            String result = builder.toString();
            return new RCommandResult(errorCode, result);
        } else {
            return new RCommandResult();
        }
    }


    public RCommandResult executeCommand(String command) throws IOException, InterruptedException {
        return executeCommand(command, true);
    }

    public RCommandResult executeCommand(String command, boolean waitFor) throws IOException, InterruptedException {
        return executeCommand(command, waitFor, false);
    }

    public RCommandResult executeCommand(String command, boolean waitFor, boolean saveToWorkspace) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(workspacePath.toFile());
        if (saveToWorkspace) {
            processBuilder.command("Rscript", "--restore", "--save", "-e", command);
        } else {
            processBuilder.command("Rscript", "--restore", "-e", command);
        }
        LOGGER.debug("executing: " + String.join(" ", processBuilder.command()));
        processBuilder.start();
        Process process = processBuilder.start();
        if (waitFor) {
            int errorCode = process.waitFor();
            processBuilder.redirectErrorStream(true);
            Scanner s = new Scanner(process.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (s.hasNextLine()) {
                builder.append(s.nextLine());
            }
            String result = builder.toString();
            return new RCommandResult(errorCode, result);
        } else {
            return new RCommandResult();
        }
    }

    private String createPreparationScriptCommand(String preparationScript) {
        return "source('" + FilenameUtils.separatorsToUnix(preparationScript) + "')";
    }

    public Path getWorkspacePath() {
        return workspacePath;
    }

}
