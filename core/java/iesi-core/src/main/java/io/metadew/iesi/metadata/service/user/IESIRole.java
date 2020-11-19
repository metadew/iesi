package io.metadew.iesi.metadata.service.user;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.metadew.iesi.metadata.service.user.IESIPrivilege.*;

public enum IESIRole {
    ADMIN("ADMIN",
            Stream.of(
                    // Scripts
                    SCRIPTS_CREATE,
                    SCRIPTS_DELETE,
                    SCRIPTS_READ,
                    // Components
                    COMPONENTS_CREATE,
                    COMPONENTS_READ,
                    COMPONENTS_DELETE,
                    // Connections
                    CONNECTIONS_CREATE,
                    CONNECTIONS_READ,
                    CONNECTIONS_DELETE,
                    // Environments
                    ENVIRONMENTS_CREATE,
                    ENVIRONMENTS_READ,
                    ENVIRONMENTS_DELETE,
                    // Execution Requests
                    EXECUTION_REQUESTS_CREATE,
                    EXECUTION_REQUESTS_READ,
                    EXECUTION_REQUESTS_DELETE,
                    // Script Executions
                    SCRIPT_EXECUTIONS_CREATE,
                    SCRIPT_EXECUTIONS_READ,
                    SCRIPT_EXECUTIONS_DELETE,
                    USERS_READ,
                    TEAMS_READ,
                    ROLES_DELETE,
                    ROLES_WRITE,
                    SECURITY_GROUP_READ
            ).collect(Collectors.toSet())),
    TECHNICAL_ENGINEER("TECHNICAL_ENGINEER",
            Stream.of(
                    // Scripts
                    SCRIPTS_READ,
                    // Components
                    COMPONENTS_READ,
                    // Connections
                    CONNECTIONS_CREATE,
                    CONNECTIONS_READ,
                    CONNECTIONS_DELETE,
                    // Environments
                    ENVIRONMENTS_CREATE,
                    ENVIRONMENTS_READ,
                    ENVIRONMENTS_DELETE,
                    // Execution Requests
                    EXECUTION_REQUESTS_READ,
                    // Script Executions
                    SCRIPT_EXECUTIONS_READ,
                    USERS_READ,
                    TEAMS_READ,
                    SECURITY_GROUP_READ
            ).collect(Collectors.toSet())),
    TEST_ENGINEER("TEST_ENGINEER",
            Stream.of(
                    // Scripts
                    SCRIPTS_CREATE,
                    SCRIPTS_DELETE,
                    SCRIPTS_READ,
                    // Components
                    COMPONENTS_CREATE,
                    COMPONENTS_READ,
                    COMPONENTS_DELETE,
                    // Connections
                    CONNECTIONS_READ,
                    // Environments
                    ENVIRONMENTS_READ,
                    // Execution Requests
                    EXECUTION_REQUESTS_CREATE,
                    EXECUTION_REQUESTS_READ,
                    // Script Executions
                    SCRIPT_EXECUTIONS_READ,
                    USERS_READ,
                    TEAMS_READ,
                    SECURITY_GROUP_READ
            ).collect(Collectors.toSet())),
    EXECUTOR("EXECUTOR",
            Stream.of(
                    // Scripts
                    SCRIPTS_READ,
                    // Components
                    COMPONENTS_READ,
                    // Connections
                    CONNECTIONS_READ,
                    // Environments
                    ENVIRONMENTS_READ,
                    // Execution Requests
                    EXECUTION_REQUESTS_CREATE,
                    EXECUTION_REQUESTS_READ,
                    // Script Executions
                    SCRIPT_EXECUTIONS_READ,
                    USERS_READ,
                    TEAMS_READ,
                    SECURITY_GROUP_READ
            ).collect(Collectors.toSet())),
    VIEWER("VIEWER",
            Stream.of(
                    // Scripts
                    SCRIPTS_READ,
                    // Components
                    COMPONENTS_READ,
                    // Connections
                    CONNECTIONS_READ,
                    // Environments
                    ENVIRONMENTS_READ,
                    // Execution Requests
                    EXECUTION_REQUESTS_READ,
                    // Script Executions
                    SCRIPT_EXECUTIONS_READ,
                    USERS_READ,
                    TEAMS_READ,
                    SECURITY_GROUP_READ
            ).collect(Collectors.toSet())),
    SYS_ADMIN("SYSADMIN",
            Stream.of(
                    USERS_READ,
                    USERS_WRITE,
                    TEAMS_READ,
                    TEAMS_WRITE,
                    TEAMS_DELETE,
                    ROLES_DELETE,
                    ROLES_WRITE,
                    SECURITY_GROUP_READ,
                    SECURITY_GROUP_WRITE,
                    SECURITY_GROUP_DELETE
            ).collect(Collectors.toSet())),
    ;

    private final String name;
    private final Set<IESIPrivilege> iesiPrivileges;

    private IESIRole(String name, Set<IESIPrivilege> iesiPrivileges) {
        this.name = name;
        this.iesiPrivileges = iesiPrivileges;
    }

    public String getName() {
        return name;
    }

    public Set<IESIPrivilege> getIesiPrivileges() {
        return iesiPrivileges;
    }
}
