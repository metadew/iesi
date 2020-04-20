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
  table_id   = "bar"

  time_partitioning {
    type = "DAY"
  }

  labels = {
    env = "default"
  }

  schema = <<EOF
[
  {
    "name": "permalink",
    "type": "STRING",
    "mode": "NULLABLE",
    "description": "The Permalink"
  },
  {
    "name": "state",
    "type": "STRING",
    "mode": "NULLABLE",
    "description": "State where the head office is located"
  }
]
EOF

}