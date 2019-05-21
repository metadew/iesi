FROM microsoft/mssql-server-linux:latest

# Adjusted from https://github.com/twright-msft/mssql-node-docker-demo-app under MIT License

# Create app directory
RUN mkdir -p /usr/src
WORKDIR /usr/src

# Bundle app source
COPY ./init /usr/src

# Grant permissions for the import-data script to be executable
RUN chmod +x /usr/src/import-data.sh
RUN chmod +x /usr/src/entrypoint.sh

CMD /bin/bash ./entrypoint.sh