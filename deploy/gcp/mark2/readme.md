# Mark1 deployment on GCP

This configuration will perform a Mark2 deployment using Terraform.

## General prerequisites
* Make sure that Terraform is up-and-running, if not have a look [here](https://learn.hashicorp.com/terraform/gcp/intro)
* Select a project to deploy to. If none exists, create a new project.
* Enable the Google Compute Engine API
* Enable the Cloud Resource Manager API
* Create a service account for Terraform to do the necessary work for you.
* Ensure the service account is assigned with the ```roles/servicenetworking.networksAdmin``` role

You can find more information on the above three steps [here](https://learn.hashicorp.com/terraform/gcp/build).

## Template prerequisites
* Select a network to use. Feel free to create a new one depending on your needs.
* The iesi-instance will be created with the option `os login` enabled. Click [here](https://cloud.google.com/compute/docs/oslogin) to learn more.

## Getting started
* Complete the parameters in the `setenv.sh` file and source it
* Run the following commands with the ```tf-launch.sh``` script

```
./tf-launch.sh init
./tf-launch.sh apply
./tf-launch.sh destroy
```

And you are ready to go! Check it out in the console!

## Components

* google_compute_network -> custom VPC network
* google_compute_subnetwork -> custom VPC subnetwork
* google_compute_global_address
* google_service_networking_connection -> private VPC peering for Cloud SQL
* google_compute_address
* google_compute_instance -> empty instance
  * google_compute_address -> static IP address
  * google_compute_firewall -> SSH access
* google_sql_database_instance
  * google_sql_database
  * google_sql_user -> admin user

## Important attention points
* Mark2 makes use of private VPC peering. This is incorrectly destroyed by Terraform and will require a manual action in the console before or after running the destruction.

## Open points
* Review variables and input, split between setenv.sh and terraform.tfvars
* Remove the public IP for the CloudSQL instance