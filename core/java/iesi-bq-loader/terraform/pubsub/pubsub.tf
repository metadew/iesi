# Ensure pubsub API has been enabled
resource "google_pubsub_topic" "pubsub_scriptresults" {
  project = var.project
  name = "iesi-scriptresults"
}

resource "google_pubsub_subscription" "pubsub_scriptresults" {
  name  = "iesi-scriptresults-bigquery"
  project = var.project
  topic = google_pubsub_topic.pubsub_scriptresults.name
  ack_deadline_seconds = 20
}