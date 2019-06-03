{% include navigation.html %}

# Do more with... shell commands

This page highlights several options for getting more value out of shell commands using automation scripts. 
All examples below can be configured using the `cli.executeCommand` action type.

## Basic testing using standard commands

### Checking if a file exists on a system

Listing the file that needs to be checked. If the file is found the operating system will return a successful return code, if not an error return code is provided.

```bash
ls file.ext
```


