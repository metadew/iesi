#!/bin/bash
current_dir=$(pwd)
script_dir=`dirname "${BASH_SOURCE[0]}"`

export GOOGLE_APPLICATION_CREDENTIALS="${script_dir}/service-account.json"
export tf_state_bucket=
export tf_state_path=iesi-bq-loader/terraform.tfstate
export tf_target_project=

