FROM maven:3-jdk-8 AS staging
WORKDIR /app

ADD docker /app/docker
ADD licenses /app/licenses
ADD LICENSE /app/LICENSE
ADD build/ext /app/build/ext
ADD core/assembly /app/core/assembly
ADD core/bin /app/core/bin
ADD core/conf /app/core/conf
ADD core/data /app/core/data
ADD build/ext /app/build/ext
ADD core/java/iesi-core/pom.xml /app/core-pom.xml
ADD core/java/iesi-core /app/core/java/iesi-core

ADD core/java/iesi-rest-without-microservices/pom.xml /app/rest-pom.xml
ADD core/java/iesi-rest-without-microservices /app/core/java/iesi-rest-without-microservices


WORKDIR /app/build/ext
RUN mvn install:install-file -Dfile=xeger-1.0.jar -DgroupId=nl.flotsam -DartifactId=xeger -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=artifactory-java-client-api-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=api -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=artifactory-java-client-httpClient-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=httpClient -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=artifactory-java-client-ning-services-2.5.1.jar -DgroupId=artifactory-java-client -DartifactId=ning-services -Dversion=2.5.1 -Dpackaging=jar
RUN mvn install:install-file -Dfile=artifactory-java-client-services-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=services -Dversion=2.6.0 -Dpackaging=jar

WORKDIR /app
RUN mvn verify --fail-never

WORKDIR /app/core/java/iesi-core
RUN mvn install -Dmaven.test.skip=true -Dmaven.deploy.skip=true -Dgpg.skip

WORKDIR /app/core/java/iesi-rest-without-microservices
RUN mvn install -Dmaven.test.skip=true -Dmaven.deploy.skip=true -Dgpg.skip

WORKDIR /app/core/java/iesi-core/target
RUN java -jar iesi-core-0.11.0-SNAPSHOT-exec.jar -launcher assembly -repository /app -sandbox /app/sandbox -instance assembly -version 0.11.0-SNAPSHOT

WORKDIR /app/sandbox/0.11.0-SNAPSHOT/assembly
RUN chmod ug+x bin/*.sh

FROM debian:10
RUN apt-get update && apt-get -y install software-properties-common gettext-base procps
RUN apt-add-repository 'deb http://security.debian.org/debian-security stretch/updates main' && apt-get update
RUN apt-get -y install openjdk-8-jdk
COPY --from=staging /app/sandbox/0.11.0-SNAPSHOT/assembly /opt/iesi
COPY --from=staging /app/docker/application.yml.template /opt/iesi/conf/application.yml
COPY --from=staging /app/docker/application-repository.yml.template /opt/iesi/conf/application-repository.yml

ENV PORT 8080
ENV IESI_HOME /opt/iesi
ENV IESI_WORKER_PATH /opt/iesi
ENV IESI_MASTER_PATH /opt/iesi
ENV IESI_MASTER_USER ""
ENV IESI_MASTER_PASSWORD ""
ENV HOST http://127.0.0.1:$PORT

EXPOSE $PORT
ENTRYPOINT ["/opt/iesi/bin/rest-docker.sh"]