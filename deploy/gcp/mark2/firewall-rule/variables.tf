variable "dependencies" {
  description = "Create a dependency between the resources in this module to the interpolated values in this list (and thus the source resources)."
  type        = list(string)
  default     = []
}

variable "network" {
  description = "The network to deploy to"
  type    = string
}

variable "name" {
  description = "The name of the firewall rule"
  type    = string
}

variable "description" {
  description = "The description of the firewall rule"
  type    = string
}

variable "protocol" {
  description = "The name of the protocol to allow or deny"
  type    = string
}

variable "ports" {
  description = "The port numbers to allow or deny"
  type    = list
}

variable "allow-rule" {
  description = "Is this an allow rule?"
  type    = bool
}

variable "source_ranges" {
  description = "The source ranges"
  type    = list
}

variable "target_tags" {
  description = "The target tags"
  type    = list
}
