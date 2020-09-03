{% include navigation.html %}

# db.bigquery

This type connects to Google BigQuery. It uses the Magnitude Simba drivers for BigQuery. More information can be found [here](https://cloud.google.com/bigquery/providers/simba-drivers). More technical information can be found [here](https://www.simba.com/drivers/bigquery-odbc-jdbc/).

Get more information at [https://cloud.google.com/bigquery](https://cloud.google.com/bigquery).

## Use cases

* Connect to database

## Parameters

### 1: host

`host: "host name"`
* define the host name of the server

### 2: port

`port: "port number"`
* define the port number for connecting to the host

### 3: project

`project: "project name"`
* define the name of the Google Cloud project

### 4: authValue

`authValue: "authValue number"`
* define the number that specifies the type of authentication used by the driver
  * 0: The driver uses service-based OAuth authentication
  * 1: The driver uses user-based OAuth authentication
  * 2: The driver uses pre-generated tokens for authentication
  * 3: The driver uses Application Default Credentials for authentication

### 5: authValue defined parameters

Depending on the authValue number specified, the following parameters need to be specified.

*authValue 0: using a Google Serice Account*

|Parameter|Value|
|------|-----------|
|OAuthServiceAcctEmail|Google service account email address|
|OAuthPvtKeyPath|full path to the key file that is used to authenticate the service account email address. This parameter supports keys in .pl2 or .json format.|

*authValue 1: using a Google User Account*

Not available for the moment. Please raise an issue if this is relevant to you.

*authValue 2: Using Pre-Generated Access and Refresh Tokens*

Not available for the moment. Please raise an issue if this is relevant to you.

|Parameter|Value|
|------|-----------|
|OAuthAccessToken|access token|

OR

|Parameter|Value|
|------|-----------|
|OAuthRefreshToken|refresh token|
|OAuthClientId|client id|
|OAuthClientSecret|client secret|

*authValue 3: Using Application Default Credentials*

* If the environment variable GOOGLE_APPLICATION_CREDENTIALS is set, Application Default Credentials uses the service account file that the variable points to.
* If the environment variable GOOGLE_APPLICATION_CREDENTIALS isn't set, Application Default Credentials uses the default service account that Compute Engine, Google Kubernetes Engine, App Engine, Cloud Run, and Cloud Functions provide.
* If Application Default Credentials can't use either of the above credentials, an error occurs.

See [here](https://cloud.google.com/docs/authentication/production) for more information.


## Examples

```yaml
---
type: Connection
data:
  name: "db.postgresql.1"
  type: "db.postgresql"
  description: "db.postgresql connection"
  environment: "iesi-test"
  parameters:
  - name: "host"
    value: "localhost"
  - name: "port"
    value: "5432"
  - name: "database"
    value: "iesi"
  - name: "user"
    value: "admin"
  - name: "password"
    value: "ENC(Waan0DOjDHNBnTXtldp5xnGwIP0=)"
```