# DB2

## Quickstart

Reference: https://www.metabase.com/start/docker.html


1. Spin up docker environment

docker run -d -p 3000:3000 --name demo4_metabase metabase/metabase

docker run -d -p 3000:3000 -v ./metabase-data:/metabase-data -e "MB_DB_FILE=/metabase-data/metabase.db" --name demo4_metabase metabase/metabase

2. Connect to metabase from

http://localhost:3000

