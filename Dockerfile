#Stage 1 - Install dependencies and build the app
FROM maven:3-jdk-8 AS staging
ENV APP /app
WORKDIR $APP
# Set working directory
WORKDIR /app
# Copy all files from current directory to working dir in image

ADD build/ext $APP/build/ext
ADD core/assembly $APP/core/assembly
ADD core/bin $APP/core/bin
ADD core/data $APP/core/data
ADD core/conf $APP/core/conf
ADD core/java/iesi-core $APP/core/java/iesi-core
ADD core/java/iesi-rest-without-microservices $APP/core/java/iesi-rest-without-microservices
ADD licenses $APP/licenses
ADD LICENSE $APP/LICENSE


RUN --mount=type=cache,target=/root/.m2 mvn install:install-file -Dfile=build/ext/xeger-1.0.jar -DgroupId=nl.flotsam -DartifactId=xeger -Dversion=1.0 -Dpackaging=jar
RUN --mount=type=cache,target=/root/.m2 mvn install:install-file -Dfile=build/ext/artifactory-java-client-api-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=api -Dversion=2.6.0 -Dpackaging=jar
RUN --mount=type=cache,target=/root/.m2 mvn install:install-file -Dfile=build/ext/artifactory-java-client-httpClient-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=httpClient -Dversion=2.6.0 -Dpackaging=jar
RUN --mount=type=cache,target=/root/.m2 mvn install:install-file -Dfile=build/ext/artifactory-java-client-ning-services-2.5.1.jar -DgroupId=artifactory-java-client -DartifactId=ning-services -Dversion=2.5.1 -Dpackaging=jar
RUN --mount=type=cache,target=/root/.m2 mvn install:install-file -Dfile=build/ext/artifactory-java-client-services-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=services -Dversion=2.6.0 -Dpackaging=jar
WORKDIR $APP/core/java/iesi-core
RUN --mount=type=cache,target=/root/.m2 mvn -P dependencies clean install project-info-reports:dependencies -Dmaven.test.skip=true
WORKDIR $APP/core/java/iesi-rest-without-microservices
RUN --mount=type=cache,target=/root/.m2 mvn clean install project-info-reports:dependencies -Dmaven.test.skip=true
WORKDIR $APP/core/java/iesi-core/target
RUN java -cp iesi-core-0.8.0-jar-with-dependencies.jar io.metadew.iesi.launch.AssemblyLauncher -repository /app -sandbox /app/sandbox -instance assembly -version 0.8.0
WORKDIR /app/sandbox/0.8.0/assembly
RUN chmod ug+x bin/*.sh

FROM debian:10
RUN apt-get update && apt-get -y install software-properties-common gettext-base procps
RUN apt-add-repository 'deb http://security.debian.org/debian-security stretch/updates main' && apt-get update
RUN apt-get -y install openjdk-8-jdk
COPY --from=staging /app/sandbox/0.8.0/assembly /opt/iesi
ENV DATABASE_CONNECTION_URL ../repository.db3
ENV DATABASE_TYPE sqlite
ENV DATABASE_USER ''
ENV DATABASE_PASSWORD ''

ENV IESI_HOME /opt/iesi
ENV IESI_WORKER_PATH /opt/iesi
ENV IESI_MASTER_PATH /opt/iesi

ENV PORT 8080
ENV HOST 0.0.0.0

RUN . ~/.bashrc
RUN /bin/bash -c "envsubst < /opt/iesi/conf/application-repository.yml | tee /opt/iesi/conf/application-repository.yml"
RUN /bin/bash -c "envsubst < /opt/iesi/conf/application.yml | tee /opt/iesi/conf/application.yml"

RUN /opt/iesi/bin/iesi-metadata.sh -type general -create

EXPOSE $PORT
CMD sh -c "/opt/iesi/bin/iesi-rest.sh"
