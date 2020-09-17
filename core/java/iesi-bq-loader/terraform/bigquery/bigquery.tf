resource "google_bigquery_dataset" "main" {
  dataset_id    = "iesi_results"
  friendly_name = "iesi results"
  description   = "contains iesi result metadata"
  location      = var.location

  project       = var.project
  labels        = {
    env = "default"
  }
}

resource "google_bigquery_table" "default" {
  dataset_id = google_bigquery_dataset.main.dataset_id
  table_id   = "res_script"
  schema = file("schema/bq_res_script.json")

  labels = {
    env = "default"
  }
}