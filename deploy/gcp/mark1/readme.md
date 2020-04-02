# Mark1 deployment on GCP

This configuration will perform a Mark1 deployment using Terraform.

## General prerequisites
* Make sure that Terraform is up-and-running, if not have a look [here](https://learn.hashicorp.com/terraform/gcp/intro)
* Select a project to deploy to. If none exists, create a new project.
* Enable the Google Compute Engine API
* Create a service account for Terraform to do the necessary work for you.

You can find more information on the above three steps [here](https://learn.hashicorp.com/terraform/gcp/build).

## Template prerequisites
* Select a network to use. Feel free to create a new one depending on your needs.
* The iesi-instance will be created with the option `os login` enabled. Click [here](https://cloud.google.com/compute/docs/oslogin) to learn more.

## Getting started
* Complete the parameters in the `terraform.tfvars` file
* Run the following commands

```
terraform init
terraform plan
terraform apply
```

And you are ready to go! Check it out in the console!
