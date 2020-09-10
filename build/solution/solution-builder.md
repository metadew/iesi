{% include navigation.html %}

# Build the solution

This page describes how to build a branch from git and assemble the distribution.

## Builder script

### Limitations

* The builder script is currently made for debian based linux environments using the bash shell scripting language.
* The builder script only works if the `build/solution` folder is part of the target branch.

### Accessing the script

The builder script can be found in the `build/solution` folder.
* Verify the desired iesi_version and update as needed

### Script actions

The builder script automates the following steps that are needed:
* update the apt package manager
* install maven if necessary
* clone the requested branch fron git
* install any library that is not present in maven central locally
* build the source code using maven
  * iesi-core
  * iesi-rest
  * iesi-test
* setup a workspace for deploying the assembly to
* create the solution assembly
* create a distribution artefact

### Script arguments

The script can take several arguments:

|argument|description|default|
|---|---|---|
|-d=[arg] or -dir=[arg]|The build directory where to run from|current directory|
|-b=[arg] or -branch=[arg]|The branch to build|develop|
|-e=[arg] or -exclude=[arg]|The actions to exclude when running the script||

For the exclusion of action, there are several options as listed below:

|exclusion|action|
|---|---|
|g|clone the requested branch fron git|
|m|install any library that is not present in maven central locally|
|b|build the source code using maven|
|w|setup a workspace for deploying the assembly to|
|a|create the solution assembly|
|d|create a distribution artefact|

Exclusions are added as single characters that are concatenated one after the other. For instance:
* `-e=g`: excludes the clone from git
* `-e=gmb`: exclude the clone from git, local maven install and source code build

**Important:**
* The script can be found in the iesi git repository, however it is not the objective to run the script from inside the folder where it is located.
* Instead, create another directory outside the structure to run it in.
* Feel free to copy the script to the build directory
* Exclude any actions as needed to quickly re-process parts of the script

### Script output

As a result, the script will produce the following inside the build directory:
* [source]/[branch]/[git clone] containing the code that has been built
* workspace/[version]/[build] containing the assembled solution
* workspace/dist/[branch] containing the compressed archives suffixed with a timestamp: ```iesi-dist-[timestamp].tar.gz```

Happy building!!