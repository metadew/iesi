{% include navigation.html %}

# Important highlights

## Installation

* We have noticed mixed experiences when installing our framework on a Microsoft One Drive synced folder. We recommend an installation on a local drive.

## Naming conventions

* There is no naming standard imposed by the framework
* Nevertheless, it is possible to define a guideline for naming configuration items consistently and make them more readible. 
The use of prefixes is commonly applied for this reason. 
* Naming of variables is very important since a variable name needs to be unique within the execution context; 
If a same variable name is loaded, the previous value is overwritten

## Operating system

* For path names on Windows, also make use of / instead of \
* Scripts for Windows and Linux are located in the same folders and need to be used appropriately

## Encryption

* All passwords are encrypted using the AES algorithm
* Encrypted parameter values are always encoded using the following syntax: ENC([EncryptedValue])
* Encrypted parameters and their decrypted values are redacted from logs by default
