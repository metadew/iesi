#Stage 1 - Install dependencies and build the app
FROM maven:3-jdk-8 AS staging
# Set working directory
WORKDIR /app
# Copy all files from current directory to working dir in image

COPY ./ /app/
RUN mvn install:install-file -Dfile=build/ext/xeger-1.0.jar -DgroupId=nl.flotsam -DartifactId=xeger -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-api-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=api -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-httpClient-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=httpClient -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-ning-services-2.5.1.jar -DgroupId=artifactory-java-client -DartifactId=ning-services -Dversion=2.5.1 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-services-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=services -Dversion=2.6.0 -Dpackaging=jar
WORKDIR /app/core/java/iesi-core
RUN mvn -Dmaven.test.skip=true install
RUN mvn -P dependencies validate project-info-reports:dependencies
WORKDIR /app/core/java/iesi-rest-without-microservices
RUN mvn -Dmaven.test.skip=true package
RUN mvn project-info-reports:dependencies
WORKDIR /app/core/java/iesi-core/target
RUN java -cp iesi-core-0.7.0-jar-with-dependencies.jar io.metadew.iesi.launch.AssemblyLauncher -repository /app -sandbox /app/sandbox -instance assembly -version 0.7.0
WORKDIR /app/sandbox/0.7.0/assembly
RUN chmod ug+x bin/*.sh

FROM debian:10
RUN apt-get update && apt-get -y install software-properties-common gettext-base
RUN apt-add-repository 'deb http://security.debian.org/debian-security stretch/updates main' && apt-get update
RUN apt-get -y install openjdk-8-jdk
COPY --from=staging /app/sandbox/0.7.0/assembly /opt/iesi
COPY --from=staging /app/docker/application-repository.yml.template /opt/iesi/conf/application-repository.yml.template
ENV DATABASE_CONNECTION_URL jdbc:sqlite:../repository.db3
ENV DATABASE_TYPE sqlite
ENV DATABASE_USER ''
ENV DATABASE_PASSWORD '' 
PORT 8080
CMD sh -c "envsubst '\$DATABASE_CONNECTION_URL:\$DATABASE_CONNECTION_URL,\$DATABASE_TYPE:\$DATABASE_TYPE,\$DATABASE_USER:\$DATABASE_USER,\$DATABASE_PASSWORD:\$DATABASE_PASSWORD' < /opt/iesi/conf/application-repository.yml.template > /opt/iesi/conf/application-repository.yml && /opt/iesi/bin/iesi-backend.sh"
