{% include navigation.html %}
## socket.transmitMessage
## Purpose
This actiontype executes a socket message

*Use Cases*
* Use cases where messages are communicated over a socket connection

## Fields

|Parameter|Description|Type|Mandatory|Encrypted|
|---------|-----------|----|---------|---------|
|socket|Name of socket connection|string|Y|N|
|message|Message to transmit|string|Y|N|
|protocol|What protocol to use (TCP/UDP)|string|Y|N|
|output|Reference name of the output dataset|string|N|N|
|timeout|timeout value to wait for response|string|N|N|


## Example
TBD
