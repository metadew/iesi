#!/bin/bash
bin_dir=$(dirname "${BASH_SOURCE[0]}")
# Start the first process
$bin_dir/iesi-server.sh &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start IESI Server: $status"
  exit $status
fi

# Start the second process
$bin_dir/iesi-rest.sh &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start IESI Rest Server: $status"
  exit $status
fi

# Naive check runs checks once a minute to see if either of the processes exited.
# This illustrates part of the heavy lifting you need to do if you want to run
# more than one service in a container. The container exits with an error
# if it detects that either of the processes has exited.
# Otherwise it loops forever, waking up every 60 seconds

while sleep 60; do
  ps aux |grep iesi-server.sh |grep -q -v grep
  IESI_SERVER_STATUS=$?
  ps aux |grep iesi-rest.sh |grep -q -v grep
  IESI_REST_SERVER_STATUS=$?
  # If the greps above find anything, they exit with 0 status
  # If they are not both 0, then something is wrong
  if [ $IESI_SERVER_STATUS -ne 0 -o $IESI_REST_SERVER_STATUS -ne 0 ]; then
    echo "One of the processes has already exited."
    exit 1
  fi
done
