variable "credentials_file" {
  description = "The credentials file for deploying the configuration"
  type    = string
}

variable "project" {
  description = "The project to deploy to"
  type    = string
}

variable "network" {
  description = "The network to deploy to"
  type    = string
}

variable "region" {
  description = "The region to deploy to"
  type    = string
}

variable "zone" {
  description = "The zone to deploy to"
  type    = string
}
