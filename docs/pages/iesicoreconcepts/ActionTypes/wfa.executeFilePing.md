{% include navigation.html %}
## wfa.executeFilePing
## Purpose
This actiontype waits until a file statement returns a result or not

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|filePath|Path where the file(s) need to be located|string|Y|N|
|fileName|File name or expression to check for availability|string|Y|N|
|hasResult|lag indicating if the file should be available or not|boolean|Y|N|
|setRuntimeVariables|Flag indicating if an expected result will be set as a runtime|boolean|N|N|
|connection|Connection where the file(s) will be located|string|Y|N|
|wait|Number of seconds to wait between checks|number|N|N|
|timeout|Number of seconds to wait for a result|number|N|N|

## Example
TBD
