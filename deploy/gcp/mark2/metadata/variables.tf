variable "dependencies" {
  description = "Create a dependency between the resources in this module to the interpolated values in this list (and thus the source resources)."
  type        = list(string)
  default     = []
}

variable "project" {
  description = "The project to deploy to"
  type    = string
}

variable "region" {
  description = "The region to deploy to"
  type    = string
}

variable "private_network" {
  description = "The private network to deploy to"
  type    = string
}

variable "database_version" {
  description = "The database version to use"
  type    = string
}