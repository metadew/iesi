{% include navigation.html %}

# host.unix

This connection is for unix based host systems. Remote connections require ssh to be active.

## Use cases

* Connect to remote hosts

## Parameters

### 1: host

`host: "host name"`
* define the host name to connect to
* this can be the logical name or the ip address

### 2: port

`port: "port number"`
* define the port number to use for the ssh connection

### 3: user

`user: "user name"`
* define the user name to use for the establishing the connection

### 4: password

`password: "user password"`
* define the user password to use for establishing the connection

### 5: tempPath

`tempPath: "temporary work directory"`
* define a temporary directoy path that can be used by the framework
* the user define above needs to have read, write and execute rights on this directory

### 6: simulateTerminal

`simulateTerminal: "Y" / "N"`
* flag indicating if a terminal (tty) needs to be simulated when connecting

### 7: simulateTerminal

`jumphostConnections: "list of connections"`
* connection names that need to be used as jump host for connecting
* these connection also need to be host.linux connections
* several connection names need to be separated by a comma: `connection1,connection2`

### 8: allowLocalhostExecution

`allowLocalhostExecution: "Y" + "N"`
* flag indicating if processes are allowed to run as localhost on the automation engine
* if this flag is set to `Y`, processes on the localhost are executed by the framework with the framework user
* if this flag is set to `N`, a ssh connection with the user name and password is established before executing processes

## Examples

```yaml
---
type: Connection
data:
  name: "host.unix.1"
  type: "host.unix"
  description: "host.unix.1 connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "2222"
  - name: "user"
    value: "root"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
  - name: "tempPath"
    value: "/tmp"
  - name: "simulateTerminal"
    value: "N"
```
