
output "instance_address" {
  description = "The IPv4 address of the iesi-metadata-instance database"
  value       = google_sql_database_instance.instance.ip_address.0.ip_address
}

output "generated_password" {
  description = "The auto generated default password for iesi-admin"
  value       = random_id.iesi-admin-password.hex
  sensitive   = true
}


