provider "google" {
  version = "3.5.0"

  credentials = file(var.credentials_file)

  project = var.project
  region = var.region
  zone = var.zone
  
}

# ------------------------------------------------------------------------------
# CREATE A RANDOM SUFFIX AND PREPARE RESOURCE NAMES
# ------------------------------------------------------------------------------

resource "random_id" "name" {
  byte_length = 2
}

locals {
  # If name_override is specified, use that - otherwise use the name_prefix with a random string
  private_network_name = "iesi-private-network-${random_id.name.hex}"
  private_ip_name      = "private-ip-${random_id.name.hex}"
}

# ------------------------------------------------------------------------------
# CREATE COMPUTE NETWORKS
# ------------------------------------------------------------------------------

# Custom mode network
resource "google_compute_network" "private_network" {
  name     = local.private_network_name
  auto_create_subnetworks = false
  delete_default_routes_on_create = false
  routing_mode            = "REGIONAL"
}

# Create the subnetwork
resource "google_compute_subnetwork" "private_network_subnet" {
  name          = "iesi-subnetwork"
  ip_cidr_range = "10.2.0.0/16"
  region        = var.region
  network       = google_compute_network.private_network.id
}

# Reserve global internal address range for the peering
resource "google_compute_global_address" "private_ip_address" {
  name          = local.private_ip_name
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.private_network.self_link
}

# Establish VPC network peering connection using the reserved address range
resource "google_service_networking_connection" "private_vpc_connection" {
  network                 = google_compute_network.private_network.self_link
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip_address.name]
}


# ------------------------------------------------------------------------------
# CREATE INSTANCE
# ------------------------------------------------------------------------------

module "instance" {
  source = "./instance"
  network=google_compute_network.private_network.self_link
  subnetwork = google_compute_subnetwork.private_network_subnet.self_link
  
  # Wait for the vpc connection to complete
  dependencies = [google_service_networking_connection.private_vpc_connection.network]
}

# ------------------------------------------------------------------------------
# CREATE FIREWALL RULE TO ACCESS THE INSTANCE
# ------------------------------------------------------------------------------


module "firewall-rule" {
  source= "./firewall-rule"
  name = "ssh-rule"
  description = "ssh-rule"
  network = local.private_network_name
  allow-rule = true
  protocol = "tcp"
  ports = ["22"]
  source_ranges = ["0.0.0.0/0"]
  target_tags = ["iesi-instance"]

  # Wait for the vpc connection to complete
  dependencies = [google_service_networking_connection.private_vpc_connection.network]
}

# ------------------------------------------------------------------------------
# CREATE METADATA
# ------------------------------------------------------------------------------

module "metadata" {
  source = "./metadata"
  project=var.project
  region = var.region
  private_network = google_compute_network.private_network.self_link
  
  # Wait for the vpc connection to complete
  dependencies = [google_service_networking_connection.private_vpc_connection.network]
}


