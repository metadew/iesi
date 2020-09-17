#!/bin/bash
parameters="-var="credentials_file=${GOOGLE_APPLICATION_CREDENTIALS}" -var="project=${tf_target_project}""

case $1 in
"init")
  terraform init -backend-config "bucket=${tf_state_bucket}" --backend-config "path=${tf_state_path}"
;;
"apply")
  terraform apply ${parameters}
;;
"destroy")
  terraform destroy ${parameters}
;;
"cleanup")
  rm -rf .terraform
;;
*)
  echo "Please provide a valid option: init, apply, destroy"
;;
esac
