variable "network" {
  description = "The network to deploy to"
  type    = string
}

variable "subnetwork" {
  description = "The subnetwork to deploy to"
  type    = string
}

variable "dependencies" {
  description = "Create a dependency between the resources in this module to the interpolated values in this list (and thus the source resources)."
  type        = list(string)
  default     = []
}