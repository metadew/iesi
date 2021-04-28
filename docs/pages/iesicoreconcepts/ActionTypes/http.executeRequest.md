{% include navigation.html %}
## http.executeRequest
## Purpose
This actiontype executes a http request

*Use Cases*
* API Testing
* Cross-channel use cases

## Fields
|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|request|Name of the request to use|string|Y|N|
|setRuntimeVariables|Flag indicating if an expected result will be set as a runtime variable|string|N|N|
|body|Body of the request|string|N|N|
|setDataset|Output datset to store the response|string|N|N|
|expectedStatusCodes|Expected status codes from response|string|N|N|
|proxy|Reference name of the proxy connection to use|string|N|N|


## Example
```yaml
  - number: 1
    type: "http.executeRequest"
    name: "Action1"
    description: "sepa transfer"
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    parameters:
    - name: "request"
      value : "sepa.transfer"
    - name: "body"
      value : ""origin_iban": "BE54000000000000","remote_iban": "BE78000000000001", "amount": 100.01, "currency": "EUR"}
    - name: "setDataset"
      value : "sepaTransferOutput"
    - name: "expectedStatusCodes"
      value : "200"
```
