resource "null_resource" "dependency_getter" {
  provisioner "local-exec" {
    command = "echo ${length(var.dependencies)}"
  }
}

resource "google_compute_address" "vm_static_ip" {
  depends_on = [null_resource.dependency_getter]
  name = "iesi-instance-static-ip"
}

resource "google_compute_instance" "vm_instance" {
  name         = "iesi-instance"
  machine_type = "e2-micro"
  tags         = ["iesi-instance"]
  metadata = {
    enable-oslogin="TRUE"
  }

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-9"
    }
  }

  network_interface {
    network=var.network
    subnetwork=var.subnetwork
    access_config {
     nat_ip = google_compute_address.vm_static_ip.address
    }
  }
  
}