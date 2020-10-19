# Multi-stage
# 1) Node image for building frontend assets
# 2) nginx stage to serve frontend assets

# Name the node stage "staging"
FROM maven:3-jdk-8 AS staging
# Set working directory
WORKDIR /app
# Copy all files from current directory to working dir in image

COPY ./ /app/
# Install dependencies into Maven repository
RUN mvn install:install-file -Dfile=build/ext/xeger-1.0.jar -DgroupId=nl.flotsam -DartifactId=xeger -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-api-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=api -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-httpClient-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=httpClient -Dversion=2.6.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-ning-services-2.5.1.jar -DgroupId=artifactory-java-client -DartifactId=ning-services -Dversion=2.5.1 -Dpackaging=jar
RUN mvn install:install-file -Dfile=build/ext/artifactory-java-client-services-2.6.0.jar -DgroupId=artifactory-java-client -DartifactId=services -Dversion=2.6.0 -Dpackaging=jar
RUN mvn -f core/java/iesi-core -Dmaven.test.skip=true -P dependencies install project-info-reports:dependencies
RUN mvn -f core/java/iesi-rest-without-microservices -Dmaven.test.skip=true package project-info-reports:dependencies
RUN java -cp core/java/iesi-core/target/iesi-core-0.6.0.jar:core/java/iesi-core/target/dependencies/* io.metadew.iesi.launch.AssemblyLauncher -repository . -sandbox build -version 0.6.0 -instance build


# nginx state for serving content
FROM openjdk:8
# Set working directory to nginx asset directory
WORKDIR /opt/iesi/
# Copy static assets from staging
COPY --from=staging /app/build/0.6.0/build/ .
COPY --from=staging /app/scripts/wrapper.sh /opt/iesi/bin/
RUN chmod u+x /opt/iesi/bin/*.sh
# Containers run nginx with global directives and daemon off
ENTRYPOINT ["bin/wrapper.sh"]
