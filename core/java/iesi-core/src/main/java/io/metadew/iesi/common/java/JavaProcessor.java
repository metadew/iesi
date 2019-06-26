package io.metadew.iesi.common.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
 
 
public class JavaProcessor {
  private List<Process> processes;
 
  public JavaProcessor() {
    processes = new ArrayList< Process >();
  }
 
  public Process startProcess(final String options, final String main, final String[] arguments)
      throws IOException {
 
    ProcessBuilder processBuilder = createProcess(options, main, arguments);
    Process process = processBuilder.start();
    processes.add(process);
    return process;
  }
 
 
  private ProcessBuilder createProcess(final String options, final String main, final String[] arguments) {
    String jvm = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    //String javaHome = System.getProperty("java.home");
    String classpath = System.getProperty("java.class.path");
 
    String[] optionsArray = options.split(" ");
    List < String > command = new ArrayList <String>();
    command.add(jvm);
    command.addAll(Arrays.asList(optionsArray));
    command.add(main);
    command.addAll(Arrays.asList(arguments));
 
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    Map< String, String > environment = processBuilder.environment();
    environment.put("CLASSPATH", classpath);
    //environment.put("JAVA_HOME", javaHome);
    //environment.put("JAVA_OPTS", javaOpts);
    //processBuilder.directory(new File(workingDir));
    return processBuilder;
  }
 
 
  public void killProcess(final Process process) {
    process.destroy();
  }
 
  public void shutdown() {
    for (Process process : processes) {
      killProcess(process);
    }
  }
}