package io.metadew.iesi.metadata.service.user;

public enum IESIPrivilege {
    // Scripts
    SCRIPTS_CREATE("SCRIPTS_WRITE"),
    SCRIPTS_READ("SCRIPTS_READ"),
    SCRIPTS_DELETE("SCRIPTS_DELETE"),
    // Components
    COMPONENTS_CREATE("COMPONENTS_WRITE"),
    COMPONENTS_READ("COMPONENTS_READ"),
    COMPONENTS_DELETE("COMPONENTS_DELETE"),
    // Connections
    CONNECTIONS_CREATE("CONNECTIONS_WRITE"),
    CONNECTIONS_READ("CONNECTIONS_READ"),
    CONNECTIONS_DELETE("CONNECTIONS_DELETE"),
    // Environments
    ENVIRONMENTS_CREATE("ENVIRONMENTS_WRITE"),
    ENVIRONMENTS_READ("ENVIRONMENTS_READ"),
    ENVIRONMENTS_DELETE("ENVIRONMENTS_DELETE"),
    // Execution Requests
    EXECUTION_REQUESTS_CREATE("EXECUTION_REQUESTS_WRITE"),
    EXECUTION_REQUESTS_READ("EXECUTION_REQUESTS_READ"),
    EXECUTION_REQUESTS_DELETE("EXECUTION_REQUESTS_DELETE"),
    // Script Executions
    SCRIPT_EXECUTIONS_CREATE("SCRIPT_EXECUTIONS_WRITE"),
    SCRIPT_EXECUTIONS_READ("SCRIPT_EXECUTIONS_READ"),
    SCRIPT_EXECUTIONS_DELETE("SCRIPT_EXECUTIONS_DELETE")
    ;

    private final String privilege;

    private IESIPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getPrivilege() {
        return privilege;
    }
}
