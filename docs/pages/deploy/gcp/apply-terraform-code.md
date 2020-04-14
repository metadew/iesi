{% include navigation.html %}

# Applying Terraform code on Google Cloud Platform

This page describes how to apply the Terraform code for the IESI solution on the Google Cloud Platform (GCP)

## Pre-requisites

* [Terraform](https://www.terraform.io/) is installed and available on the path

## Assemble the configuration from git

One way to get the Terraform code and apply it is by getting the code from git and assembling it into a workspace. This means that the code is copied to this workspace and a configuration is applied on top of it.
* your configuration remains separate from the git repository and can be fetched in any relevant way (private configuration control for instance)
* new templates can be refreshed using the assembler script having the configuration automatically applied to it

**Important** The assemble script removes all providers and state information if the option is set to remove the instance.

### Get the git repository

First, clone the git repository for [iesi](https://github.com/metadew/iesi).

### Create workspace

Now that each repository is fetched, the Terraform code can be assembled in a workspace:
* create a workspace folder for sandboxing. This folder needs to reside outside of the iesi folder structure
* add a `conf` folder inside the workspace folder for storing all necessary configuration. The assembler will automatically create this during its first execution.

### Assemble the Terraform code

Once the workspace folder has been created, the Terraform code can be assembled. Each assembly has an `instance` for wich a `configuration` will be applied.
* When starting an assembly, the solution will be deployed to `[workspace]/[instance]`
* The configuration that is available in `[workspace]/conf/[configuration]`
* Each new assembly should be set to a new instance if you have active state in GCP. Otherwise the state information is lost!!

So to move forward, you need to create a folder for the configuration in the `[workspace]/conf` folder.

Next, the assembly process can be started from the shell script that can be found in the iesi repository at `deploy/gcp`. Run the `assemble.sh` script with the following arguments:
1) -w=[path] or --workspace=[path]: workspace folder [mandatory]
2) -i=[instance] or --instance=[instance]: instance name [optional, default: assembly]
3) -c=[configuration] or --configuration=[configuration]: configuration name [optional, default: default]
4) -r or --remove: remove instance [optional, default: no]

After completion, the Terraform code will be assembled to `[workspace]/[instance]`

Now, you can run Terraform as required to deploy your GCP infrastructure. However, remain careful not to destroy your Terraform state by running the assembly with the remove instance flag. For more information on Terraform state management, have a look [here](https://www.terraform.io/docs/commands/state/index.html).

As a best practice, you can organize your state management using [Terraform backends](https://www.terraform.io/docs/backends/index.html).
