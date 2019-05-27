FROM ubuntu

#ADD SOME NEEDED DEPENDENCIES
RUN apt-get update && apt-get install -y \
   nano \
   software-properties-common \
   wget

#INSTALL JAVA JRE 1.8
RUN add-apt-repository ppa:webupd8team/java
RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections
RUN apt-get update && apt-get install -y oracle-java8-installer

#SET WORKING DIRECTORY
WORKDIR /home

#GET IESI RELEASE
RUN wget https://github.com/Sroose/iesi/releases/download/v0.0.1.sam2/iesi-dist-0.0.1.sam2.tar.gz && tar -xvzf iesi-dist-0.0.1.sam2.tar.gz

#GET POSTGRES JDBC DRIVER
RUN wget https://jdbc.postgresql.org/download/postgresql-42.2.5.jar && mv postgresql-42.2.5.jar v0.0.1/lib/

#CREATE REPOSITORY CONFIG
COPY config/repo/iesi-repository.conf v0.0.1/conf/iesi-repository.conf

#POPULATE METADATA
COPY config/metadata/ v0.0.1/metadata/in/new/
RUN ls -la v0.0.1/metadata/in/new/
RUN cd v0.0.1/bin && ./iesi-metadata.sh -drop -create -load -type general

#INSTALL PENTAHO RUNNER
RUN cd /home
RUN wget  https://github.com/Sroose/PentahoDI-runner/releases/download/v8.2.0.0/PDI_RUNNER.tar.gz && tar -xvzf PDI_RUNNER.tar.gz

#Copy ETL jobs
RUN mkdir ETL
COPY config/ETL/ ETL/