package io.metadew.iesi.runtime;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionSelectOperation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Negotiator {

    public Negotiator() {
        // Set the environment
    }

    public void submitScenario(String eng_cfg, String env_nm, int app_id, int scenario_id) {
        this.submitRequest("SCENARIO", eng_cfg, env_nm, app_id, scenario_id);
    }

    public void submitTest(String eng_cfg, String env_nm, int test_id) {
        this.submitRequest("TEST", eng_cfg, env_nm, -1, test_id);
    }

    public void submitTestSet(String eng_cfg, String env_nm, int test_id) {
        this.submitRequest("TESTSET", eng_cfg, env_nm, -1, test_id);
    }

    @SuppressWarnings("unused")
    private void submitRequest(String request_type, String eng_cfg, String env_nm, int context_id, int scope_id) {
        String QueryString = "insert into que_request (que_id, request_type, eng_cfg,env_nm,context_id,scope_id,prc_id) values ((select ifnull(max(que_id),0) + 1 as QUE_ID from  que_request),'"
                + request_type + "','" + eng_cfg + "','" + env_nm + "'," + context_id + "," + scope_id + ",-1)";
    }

    public void doStg() {
        // Run a task specified by a Supplier object asynchronously
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    runScript();
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                return "Result of the asynchronous computation";
            }
        });

        // Block and get the result of the Future
        String result;
        try {
            result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void runScript() {
        // Create the framework instance
        FrameworkInstance frameworkInstance = new FrameworkInstance();

        // Create the framework execution
        Context context = new Context();
        context.setName("negotiator");
        context.setScope("");
        FrameworkExecution frameworkExecution = new FrameworkExecution(frameworkInstance, new FrameworkExecutionContext(context), null);
        // Get the Script
        ScriptConfiguration scriptConfiguration = null;
        Script script = null;
        scriptConfiguration = new ScriptConfiguration(frameworkExecution.getFrameworkInstance());
        script = scriptConfiguration.getScript("S2").get();

        ScriptExecution scriptExecution = new ScriptExecution(frameworkExecution, script);
        scriptExecution.initializeAsRootScript("DEV");
        scriptExecution.setActionSelectOperation(new ActionSelectOperation(""));
        scriptExecution.setAsynchronously(true);

        // Execute the Script
        scriptExecution.execute();

    }

}
