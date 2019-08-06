

docker run -d -p 3000:3000 -v ./demo4:/metabase-data -e "MB_DB_FILE=/metabase-data/metabase.db" --name demo4_metabase metabase/metabase

https://docs.docker.com/v17.09/engine/userguide/networking/#the-default-bridge-network

docker inspect network bridge

https://docs.docker.com/engine/reference/commandline/network_connect/