#!/bin/bash
current_file=$(readlink -m $(type -p ${0}))
absolute_dir=`dirname "${current_file}"`
iesi_home=`dirname "${absolute_dir/../}"`

echo running the IESI container ...
echo Filling configuration placeholders ...

tmp=$(mktemp)
repository_conf_file=/opt/iesi/conf/application-repository.yml
application_conf_file=/opt/iesi/conf/application.yml

envsubst < "$repository_conf_file" > "$tmp" && mv "$tmp" "$repository_conf_file"
envsubst < "$application_conf_file" > "$tmp" && mv "$tmp" "$application_conf_file"

echo Fill completed
echo Running the rest API server


$absolute_dir/iesi-rest.sh