provider "google" {
  version = "3.5.0"

  credentials = file(var.credentials_file)

  project = var.project
}

module "pubsub" {
  source = "./pubsub"
  project=var.project
}

module "bigquery" {
  source = "./bigquery"
  project= var.project
  location=""
}
