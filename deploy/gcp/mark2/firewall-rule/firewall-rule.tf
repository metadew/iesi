resource "null_resource" "dependency_getter" {
  provisioner "local-exec" {
    command = "echo ${length(var.dependencies)}"
  }
}

resource "google_compute_firewall" "firewall-rule" {
  depends_on = [null_resource.dependency_getter]
  count = var.allow-rule ? 1 : 0
  
  name      = "${var.network}-${var.name}"
  network   = var.network
  description = var.description

  allow {
    protocol = var.protocol
    ports    = var.ports
  }
  
  target_tags   = var.target_tags
  source_ranges = var.source_ranges
}