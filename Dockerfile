#Stage 1 - Install dependencies and build the app
FROM maven:3-jdk-8 AS staging

ENV VERSION 0.9.0
ENV APP /app

ARG AZURE_FEED_USER
ARG AZURE_FEED_TOKEN

WORKDIR $APP

# Copy all files from current directory to working dir in image
ADD docker $APP/docker
ADD core/assembly $APP/core/assembly
ADD core/bin $APP/core/bin
ADD core/data $APP/core/data
ADD core/conf $APP/core/conf
ADD core/java/iesi-core $APP/core/java/iesi-core
ADD core/java/iesi-rest-without-microservices $APP/core/java/iesi-rest-without-microservices
ADD licenses $APP/licenses
ADD LICENSE $APP/LICENSE



WORKDIR $APP/core/java/iesi-core
RUN mvn -s $APP/docker/settings.xml -P dependencies clean install project-info-reports:dependencies -Dmaven.test.skip=true -Dmaven.deploy.skip=true -Dgpg.skip -Dazure.feed.username=$AZURE_FEED_USER -Dazure.feed.password=$AZURE_AZURE_FEED_TOKEN
WORKDIR $APP/core/java/iesi-rest-without-microservices
RUN mvn clean install project-info-reports:dependencies
WORKDIR $APP/core/java/iesi-core/target
RUN java -jar iesi-core-$VERSION-exec.jar -launcher assembly -repository /app -sandbox /app/sandbox -instance assembly -version $VERSION
WORKDIR /app/sandbox/0.8.0/assembly
RUN chmod ug+x bin/*.sh

FROM debian:10
RUN apt-get update && apt-get -y install software-properties-common gettext-base procps
RUN apt-add-repository 'deb http://security.debian.org/debian-security stretch/updates main' && apt-get update
RUN apt-get -y install openjdk-8-jdk
COPY --from=staging /app/sandbox/$VERSION/assembly /opt/iesi
COPY --from=staging /app/docker/application.yml.template /opt/iesi/conf/application.yml
COPY --from=staging /app/docker/application-repository.yml.template /opt/iesi/conf/application-repository.yml

ENV DATABASE_TYPE oracle
ENV DATABASE_CONNECTION_URL jdbc:oracle:thin:@tcp://oracle-db:1521/XEPDB1
ENV DATABASE_SCHEMA IESI_DEV
ENV DATABASE_INIT_SQL alter session set nls_timestamp_format=\'YYYY-MM-DD\\\"T\\\" HH24:MI:SS:FF\' current_schema=IESI_DEV
ENV DATABASE_USER IESI_DEV
ENV DATABASE_PASSWORD IESI_DEV

ENV IESI_HOME /opt/iesi
ENV IESI_WORKER_PATH /opt/iesi
ENV IESI_MASTER_PATH /opt/iesi

ENV PORT 8080
ENV HOST 0.0.0.0

RUN . ~/.bashrc
RUN /bin/bash -c "envsubst < /opt/iesi/conf/application-repository.yml | tee /opt/iesi/conf/application-repository.yml"
RUN /bin/bash -c "envsubst < /opt/iesi/conf/application.yml | tee /opt/iesi/conf/application.yml"

EXPOSE $PORT
CMD sh -c "/opt/iesi/bin/iesi-rest.sh"