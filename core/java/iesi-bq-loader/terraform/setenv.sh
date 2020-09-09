#!/bin/bash
current_dir=$(pwd)
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
  # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
script_dir="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

export GOOGLE_APPLICATION_CREDENTIALS="${script_dir}/service-account.json"
export tf_state_bucket=
export tf_state_path=iesi-bq-loader/terraform.tfstate
export tf_target_project=
