#Stage 1 - Install dependencies and build the app
FROM maven:3-jdk-8 AS staging
ENV APP /app
WORKDIR $APP
# Set working directory
WORKDIR /app
# Copy all files from current directory to working dir in image

ADD docker $APP/docker
ADD build/ext $APP/build/ext
ADD core/assembly $APP/core/assembly
ADD core/bin $APP/core/bin
ADD core/data $APP/core/data
ADD core/conf $APP/core/conf
ADD core/java/iesi-core $APP/core/java/iesi-core
ADD core/java/iesi-rest-without-microservices $APP/core/java/iesi-rest-without-microservices
ADD licenses $APP/licenses
ADD LICENSE $APP/LICENSE


RUN mvn install:install-file -Dfile=build/ext/xeger-1.0.jar -DgroupId=nl.flotsam -DartifactId=xeger -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-api-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=api -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-httpClient-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=httpClient -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-ning-services-2.5.1.jar -DgroupId=artifactory-java-client -DartifactId=ning-services -Dversion=2.5.1 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-services-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=services -Dversion=2.6.0 -Dpackaging=jar
WORKDIR $APP/core/java/iesi-core
RUN mvn versions:set -DnewVersion=0.10.0-SNAPSHOT
RUN mvn -P dependencies clean install project-info-reports:dependencies -Dmaven.deploy.skip=true -Dgpg.skip
WORKDIR $APP/core/java/iesi-rest-without-microservices
RUN mvn versions:set -DnewVersion=0.10.0-SNAPSHOT
RUN mvn clean install project-info-reports:dependencies -Diesi-rest.version=0.10.0-SNAPSHOT -Dmaven.test.skip=true
WORKDIR $APP/core/java/iesi-core/target
RUN java  -jar iesi-core-0.10.0-SNAPSHOT-exec.jar -launcher assembly -repository /app -sandbox /app/sandbox -instance assembly -version 0.10.0-SNAPSHOT
WORKDIR /app/sandbox/0.10.0-SNAPSHOT/assembly
RUN chmod ug+x bin/*.sh

FROM debian:10
RUN apt-get update && apt-get -y install software-properties-common gettext-base procps
RUN apt-add-repository 'deb http://security.debian.org/debian-security stretch/updates main' && apt-get update
RUN apt-get -y install openjdk-8-jdk
COPY --from=staging /app/sandbox/0.10.0-SNAPSHOT/assembly /opt/iesi
COPY --from=staging /app/docker/application.yml.template /opt/iesi/conf/application.yml
COPY --from=staging /app/docker/application-repository.yml.template /opt/iesi/conf/application-repository.yml

ENV DATABASE_TYPE oracle
ENV DATABASE_CONNECTION_URL jdbc:oracle:thin:@tcp://oracle-db:1521/XEPDB1
ENV DATABASE_SCHEMA IESI_TEST
ENV DATABASE_INIT_SQL alter session set nls_timestamp_format=\'YYYY-MM-DD\\\"T\\\" HH24:MI:SS:FF\' current_schema=IESI_TEST
ENV DATABASE_USER IESI_TEST
ENV DATABASE_PASSWORD IESI_TEST

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