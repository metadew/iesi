{% include navigation.html %}

# db.bigquery

This type connects to Google BigQuery. It uses the Magnitude Simba drivers for BigQuery. More information can be found [here](https://cloud.google.com/bigquery/providers/simba-drivers). More technical information can be found [here](https://www.simba.com/drivers/bigquery-odbc-jdbc/).

*Important* Only the Simba driver version 4.2 is supported for now.

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

### 4: dataset

`dataset: "dataset name"`
* define the name of the dataset

### 4: authMode

`authValue: "authentication mode"`
* define the authentication mode that specifies the type of authentication used by the driver
  * ```service```: The driver uses service-based OAuth authentication
  * ```user```: The driver uses user-based OAuth authentication
  * ```token```: The driver uses pre-generated tokens for authentication
  * ```default```: The driver uses Application Default Credentials for authentication

### 5: authMode defined parameters

Depending on the authValue number specified, the following parameters need to be specified.

*authMode service: using a Google Serice Account*

|Parameter|Value|
|------|-----------|
|OAuthServiceAcctEmail|Google service account email address|
|OAuthPvtKeyPath|full path to the key file that is used to authenticate the service account email address. This parameter supports keys in .pl2 or .json format.|

*authMode user: using a Google User Account*

No additional parameters needed. However, unattended script execution is not possible since authentication in the session is required manually.

*authMode token: Using Pre-Generated Access and Refresh Tokens*

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

*authMode default: Using Application Default Credentials*

No additional parameters needed.

* If the environment variable GOOGLE_APPLICATION_CREDENTIALS is set, Application Default Credentials uses the service account file that the variable points to.
* If the environment variable GOOGLE_APPLICATION_CREDENTIALS isn't set, Application Default Credentials uses the default service account that Compute Engine, Google Kubernetes Engine, App Engine, Cloud Run, and Cloud Functions provide.
* If Application Default Credentials can't use either of the above credentials, an error occurs.

See [here](https://cloud.google.com/docs/authentication/production) for more information.


## Examples

```yaml
---
type: Connection
data:
  name: "db.bigquery.1"
  type: "db.bigquery"
  description: "db.bigquery connection"
  environment: "iesi-dev"
  parameters:
  - name: "host"
    value: "https://www.googleapis.com/bigquery/v2"
  - name: "port"
    value: "443"
  - name: "project"
    value: "iesi-01"
  - name: "dataset"
    value: "iesi"
  - name: "authMode"
    value: "service"
  - name: "serviceAccount"
    value: "serviceaccount@domain.com"
  - name: "keyPath"
    value: "/path/keyfile.json"
```