{% include navigation.html %}

# Script

A script consists of a sequence of actions that are orchestrated for execution. It makes use of parameters and in-process variables to allow a high degree 
of reusability. Logic can be written that is only know at the moment of execution. This makes it a powerful means to create intelligent scripts that 
are able to interpret the context that they are running in. Moreover, with a small set of scripts it becomes possible to scale the number of tests easily.

## Structure

### Script object

|Field|Type|Values|Description|
|---|---|---|---|
|name||||
|description||||
|type||||
|version||||
|parameter||||
|actions||||

### Version object

### Script parameters object

### Actions object

## Format

### YAML

```yaml
---
type: "script"
data:
  name: ""
  description: ""
  type: ""
  version:
    number: ?
    description: ""
  parameters: []
  actions:
  - number: ?
    type: ""
    name: ""
    description: ""
    component: ""
    condition: ""
    iteration: ""
    errorExpected: "N"
    errorStop: "N"
    retries: "N
    parameters:
	- name: ""
      value : ""
```

### JSON

