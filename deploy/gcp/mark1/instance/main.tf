provider "google" {
  version = "3.5.0"

  credentials = file(var.credentials_file)

  project = var.project
  region  = var.region
  zone    = var.zone
}

resource "google_compute_address" "vm_static_ip" {
  name = "iesi-static-ip"
}

resource "google_compute_instance" "vm_instance" {
  name         = "iesi-instance"
  machine_type = "f1-micro"
  tags         = ["iesi-instance"]
  metadata = {
    dist = "https://github.com/metadew/iesi/releases/download/v0.3.0/iesi-dist-v0.3.0.tar.gz"
    enable-oslogin="TRUE"
  }

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-9"
    }
  }

  network_interface {
    network=var.network
    access_config {
     nat_ip = google_compute_address.vm_static_ip.address
    }
  }
  
  metadata_startup_script = data.template_file.iesi-install.rendered
}

data "template_file" "iesi-install" {
  template = "${file("${path.module}/iesi-install.tpl")}"
}
