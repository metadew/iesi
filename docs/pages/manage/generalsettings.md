{% include navigation.html %}

# General settings

The general IESI settings are configured in the `application.yml` file.

## Home
It is mandatory to define the root location of the IESI installation. This location is defined using the `home` key.

```yaml
iesi:
  home: <path_to_iesi_installation>
  ...
```

## Guard
All configuration regarding authentication/authorization are placed under the Guard section. The following configuration parameters are available:
* `guard.authenticate`: define if the framework requires authentication when running scripts (`Y` or `N`)

```yaml
iesi:
  ...
  guard:
    authenticate: N
  ...
```
## Server
The IESI installation can be run in server mode. If server mode is disabled, scripts are executed synchronously. If server mode is enabled, scripts are put in a queue and processed based on resource availibility. Server mode allows for concurrent execution of multiple scripts.

Server mode is set by the `server.mode` key (`off` for disabled, `standalone` for enabled). If server mode is enabled, the following configuration has to be defined:
* `server.threads.size`: the maximum number of concurrent executions of scripts
* `server.threads.timeout`: the maximum duration (in minutes) a script can run. If this time threshold is exceeded, the script execution is aborted.

```yaml
iesi:
  ...
  server:
    mode: standalone
    threads:
      size: 4
      timeout: 30
  ...
```
