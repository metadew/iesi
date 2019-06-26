package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.ArtifactoryConnection;
import io.metadew.iesi.connection.FileConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.CompressionTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import io.metadew.iesi.script.operation.JsonInputOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FwkExecuteSuite {

    private ActionExecution actionExecution;
    private ScriptExecution scriptExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation componentName;
    private ActionParameterOperation suiteName;
    private ActionParameterOperation suiteVersion;
    private ActionParameterOperation suiteBuild;
    private ActionParameterOperation repositoryConnectionName;
    private ActionParameterOperation repositoryComponentPath;
    private ActionParameterOperation repositorySuitePath;
    private ActionParameterOperation repositoryVersionPath;
    private ActionParameterOperation repositoryBuildPath;
    private ActionParameterOperation repositoryBuildAsset;
    private ActionParameterOperation environmentName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkExecuteSuite() {

    }

    public FwkExecuteSuite(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setScriptExecution(scriptExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setComponentName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "COMP_NM"));
        this.setSuiteName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "SUITE_NM"));
        this.setSuiteVersion(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "SUITE_VERSION"));
        this.setSuiteBuild(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "SUITE_BUILD"));
        this.setRepositoryConnectionName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "REPO_CONN_NM"));
        this.setRepositoryComponentPath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "REPO_COMP_PATH"));
        this.setRepositorySuitePath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "REPO_SUITE_PATH"));
        this.setRepositoryVersionPath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "REPO_VERSION_PATH"));
        this.setRepositoryBuildPath(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "REPO_BUILD_PATH"));
        this.setRepositoryBuildAsset(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "REPO_BUILD_ASSET"));
        this.setEnvironmentName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "ENV_NM"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("comp_nm")) {
                this.getComponentName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("suite_nm")) {
                this.getSuiteName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("suite_version")) {
                this.getSuiteVersion().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("suite_build")) {
                this.getSuiteBuild().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("repo_con_nm")) {
                this.getRepositoryConnectionName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("repo_comp_path")) {
                this.getRepositoryComponentPath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("repo_suite_path")) {
                this.getRepositorySuitePath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("repo_version_path")) {
                this.getRepositoryVersionPath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("repo_build_path")) {
                this.getRepositoryBuildPath().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("repo_build_asset")) {
                this.getRepositoryBuildAsset().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("env_nm")) {
                this.getEnvironmentName().setInputValue(actionParameter.getValue());
            }
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("COMP_NM", this.getComponentName());
        this.getActionParameterOperationMap().put("SUITE_NM", this.getSuiteName());
        this.getActionParameterOperationMap().put("SUITE_VERSION", this.getSuiteVersion());
        this.getActionParameterOperationMap().put("SUITE_BUILD", this.getSuiteBuild());
        this.getActionParameterOperationMap().put("REPO_CONN_NM", this.getRepositoryConnectionName());
        this.getActionParameterOperationMap().put("REPO_COMP_PATH", this.getRepositoryComponentPath());
        this.getActionParameterOperationMap().put("REPO_SUITE_PATH", this.getRepositorySuitePath());
        this.getActionParameterOperationMap().put("REPO_VERSION_PATH", this.getRepositoryVersionPath());
        this.getActionParameterOperationMap().put("REPO_BUILD_PATH", this.getRepositoryBuildPath());
        this.getActionParameterOperationMap().put("REPO_BUILD_ASSET", this.getRepositoryBuildAsset());
        this.getActionParameterOperationMap().put("ENV_NM", this.getEnvironmentName());
    }

    public boolean execute() {
        try {
            String componentName = convertToString(getComponentName().getValue());
            String suiteName = convertToString(getSuiteName().getValue());
            String suiteVersion = convertToString(getSuiteVersion().getValue());
            String suiteBuild = convertToString(getComponentName().getValue());
            String repositoryConnectionName = convertToString(getRepositoryConnectionName().getValue());
            String repositoryComponentPath = convertToString(getRepositoryComponentPath().getValue());
            String repositorySuitePath = convertToString(getRepositorySuitePath().getValue());
            String repositoryVersionPath = convertToString(getRepositoryVersionPath().getValue());
            String repositoryBuildPath = convertToString(getRepositoryBuildPath().getValue());
            String repositoryBuildAsset = convertToString(getRepositoryBuildAsset().getValue());
            String environmentName = convertToString(getEnvironmentName().getValue());

            return execute(componentName, suiteName, suiteVersion, suiteBuild, repositoryConnectionName, repositoryComponentPath, repositorySuitePath, repositoryVersionPath, repositoryBuildPath, repositoryBuildAsset, environmentName);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean execute(String componentName, String suiteName, String suiteVersion, String suiteBuild, String repositoryConnectionName, String repositoryComponentPath, String repositorySuitePath, String repositoryVersionPath, String repositoryBuildPath, String repositoryBuildAsset, String environmentName) {
        // Get Connection
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Connection connection = connectionConfiguration.getConnection(repositoryConnectionName,
                this.getExecutionControl().getEnvName()).get();

        // Artifactory
        // *********************************************************
        // Get repository
        ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
        ArtifactoryConnection artifactoryConnection = connectionOperation.getArtifactoryConnection(connection);

        // Compile asset path
        String resolvedRepositoryAssetPath = "";
        resolvedRepositoryAssetPath += this.getExecutionControl().getExecutionRuntime().resolveActionTypeVariables(repositoryComponentPath, this.getActionParameterOperationMap());
        resolvedRepositoryAssetPath += "/";
        resolvedRepositoryAssetPath += this.getExecutionControl().getExecutionRuntime().resolveActionTypeVariables(repositorySuitePath, this.getActionParameterOperationMap());
        resolvedRepositoryAssetPath += "/";
        resolvedRepositoryAssetPath += this.getExecutionControl().getExecutionRuntime().resolveActionTypeVariables(repositoryVersionPath, this.getActionParameterOperationMap());

        String latestBuild = "";
        if (suiteBuild.trim().equalsIgnoreCase("[latest]")) {
            // Get latest package
            latestBuild = artifactoryConnection.getlatestBuild(resolvedRepositoryAssetPath, "P");
            resolvedRepositoryAssetPath += "/";
            resolvedRepositoryAssetPath += latestBuild;
        }

        resolvedRepositoryAssetPath += "/";
        String buildAsset = this.getExecutionControl().getExecutionRuntime().resolveActionTypeVariables(repositoryBuildAsset, this.getActionParameterOperationMap());
        resolvedRepositoryAssetPath += buildAsset;

        // Check folder structure
        String rootSuiteFolder = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("run.cache") + File.separator + "suites";
        FolderTools.createFolder(rootSuiteFolder);
        String rootComponentFolder = rootSuiteFolder + File.separator + this.getComponentName().getValue();
        FolderTools.createFolder(rootComponentFolder);
        String suiteFolder = rootComponentFolder + File.separator + this.getSuiteName().getValue();
        FolderTools.createFolder(suiteFolder);
        String suiteVersionFolder = suiteFolder + File.separator + this.getSuiteVersion().getValue();
        FolderTools.createFolder(suiteVersionFolder);
        String suiteBuildFolder = suiteVersionFolder + File.separator + latestBuild;
        FolderTools.deleteFolder(suiteBuildFolder, true);
        FolderTools.createFolder(suiteBuildFolder);

        // Download the build
        artifactoryConnection.downloadArtifact(resolvedRepositoryAssetPath, suiteBuildFolder + File.separator + buildAsset);

        // Untar the build
        CompressionTools.unTarFile(this.getFrameworkExecution(), suiteBuildFolder, buildAsset);
        // Untar the payload
        String buildAssetFolder = suiteBuildFolder + File.separator + FilenameUtils.removeExtension(buildAsset) + File.separator + this.getSuiteName().getValue();
        FolderTools.deleteFolder(buildAssetFolder, true);
        CompressionTools.unTarFile(this.getFrameworkExecution(), suiteBuildFolder + File.separator + FilenameUtils.removeExtension(buildAsset), this.getSuiteName().getValue() + ".tar");

        // Run the suite
        List<FileConnection> fileConnectionList = new ArrayList();
        fileConnectionList = FolderTools.getConnectionsInFolder(buildAssetFolder, "regex", ".+\\.json", fileConnectionList);
        for (FileConnection fileConnection : fileConnectionList) {
            try {
                Script script = null;

                JsonInputOperation jsonInputOperation = new JsonInputOperation(this.getFrameworkExecution(), fileConnection.getFilePath());
                script = jsonInputOperation.getScript().get();
                if (script == null) {
                    System.out.println("No script found for execution");

                    this.getActionExecution().getActionControl().increaseWarningCount();
                } else {
                    ScriptExecution scriptExecution = new ScriptExecution(this.getFrameworkExecution(), script);
                    scriptExecution.initializeAsRootScript(environmentName);
                    scriptExecution.setActionSelectOperation(new ActionSelectOperation(""));
                    scriptExecution.setExitOnCompletion(false);

                    scriptExecution.execute();

                    this.getActionExecution().getActionControl().increaseSuccessCount();
                }

            } catch (Exception e) {
                this.getActionExecution().getActionControl().increaseErrorCount();
                return false;
            }
        }
        return true;
    }


    private String convertToString(DataType field) {
        if (field instanceof Text) {
            return field.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for field",
                    field.getClass()), Level.WARN);
            return field.toString();
        }
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

    public ScriptExecution getScriptExecution() {
        return scriptExecution;
    }

    public void setScriptExecution(ScriptExecution scriptExecution) {
        this.scriptExecution = scriptExecution;
    }

    public ActionParameterOperation getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(ActionParameterOperation environmentName) {
        this.environmentName = environmentName;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getComponentName() {
        return componentName;
    }

    public void setComponentName(ActionParameterOperation componentName) {
        this.componentName = componentName;
    }

    public ActionParameterOperation getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(ActionParameterOperation suiteName) {
        this.suiteName = suiteName;
    }

    public ActionParameterOperation getSuiteVersion() {
        return suiteVersion;
    }

    public void setSuiteVersion(ActionParameterOperation suiteVersion) {
        this.suiteVersion = suiteVersion;
    }

    public ActionParameterOperation getRepositoryConnectionName() {
        return repositoryConnectionName;
    }

    public void setRepositoryConnectionName(ActionParameterOperation repositoryConnectionName) {
        this.repositoryConnectionName = repositoryConnectionName;
    }

    public ActionParameterOperation getSuiteBuild() {
        return suiteBuild;
    }

    public void setSuiteBuild(ActionParameterOperation suiteBuild) {
        this.suiteBuild = suiteBuild;
    }

    public ActionParameterOperation getRepositoryComponentPath() {
        return repositoryComponentPath;
    }

    public void setRepositoryComponentPath(ActionParameterOperation repositoryComponentPath) {
        this.repositoryComponentPath = repositoryComponentPath;
    }

    public ActionParameterOperation getRepositorySuitePath() {
        return repositorySuitePath;
    }

    public void setRepositorySuitePath(ActionParameterOperation repositorySuitePath) {
        this.repositorySuitePath = repositorySuitePath;
    }

    public ActionParameterOperation getRepositoryVersionPath() {
        return repositoryVersionPath;
    }

    public void setRepositoryVersionPath(ActionParameterOperation repositoryVersionPath) {
        this.repositoryVersionPath = repositoryVersionPath;
    }

    public ActionParameterOperation getRepositoryBuildAsset() {
        return repositoryBuildAsset;
    }

    public void setRepositoryBuildAsset(ActionParameterOperation repositoryBuildAsset) {
        this.repositoryBuildAsset = repositoryBuildAsset;
    }

    public ActionParameterOperation getRepositoryBuildPath() {
        return repositoryBuildPath;
    }

    public void setRepositoryBuildPath(ActionParameterOperation repositoryBuildPath) {
        this.repositoryBuildPath = repositoryBuildPath;
    }
}