{% include navigation.html %}

# Variable instruction

Variable instructions retrieve a specific variable that is being used by the framework.

## Framework variables

### fwk.home

`{{$fwk.home}}`
* get the home folder of the framework

Available synonyms: 
* iesi.home

### fwk.version

`{{$fwk.version}}`
* get the current version of the framework

Available synonyms: 
* fwk.v

## Runtime variables

Runtime variables are active in the general execution engine.

### run.env

`{{$run.env}}`
* get the current environment that is active in the framework

Available synonyms: 
* run.environment

### run.id

`{{$run.id}}`
* get the current active run identifier

Available synonyms: 
* run.identifier