provider "random" {
  version = "~> 2.2"
}

resource "null_resource" "dependency_getter" {
  provisioner "local-exec" {
    command = "echo ${length(var.dependencies)}"
  }
}

resource "random_id" "name" {
  byte_length = 2
}

resource "google_sql_database" "database" {
  depends_on = [null_resource.dependency_getter]
  name     = "iesi"
  #Database name cannot have unallowed characters
  project = var.project
  instance = google_sql_database_instance.instance.name
  #charset   =
  #collation =
}

resource "google_sql_database_instance" "instance" {
  depends_on = [null_resource.dependency_getter]
  name   = "iesi-metadata-${random_id.name.hex}"
  #Instance names cannot be reused for up to a week after it's deleted.
  project = var.project
  region = var.region
  #database_version = "MYSQL_5_7"
  #database_version = "POSTGRES_12"
  database_version = var.database_version
  
  settings {
    tier                        = "db-f1-micro"
    ip_configuration {
      ipv4_enabled    = false
      private_network = var.private_network
    }
    maintenance_window {
      day          = "7"
      hour         = "23"
      update_track = "stable"
    }
    disk_size        = "10"
    disk_type        = "PD_SSD"
  }
  
  timeouts {
    create = "20m"
    delete = "20m"
  }
}

resource "random_id" "iesi-admin-password" {
  byte_length = 8
}

resource "google_sql_user" "iesi-admin" {
  name     = "iesi"
  project  = var.project
  instance = google_sql_database_instance.instance.name
  password = random_id.iesi-admin-password.hex
}
