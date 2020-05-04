#!/bin/bash -e

echo "*****    Installing Java    *****"

sudo apt-get update
sudo apt-get install openjdk-8-jdk -y


echo "*****    Installing IESI    *****"

# Parameters
TEMP_DIR=/tmp/iesi-install
TARGET_DIR=/opt/iesi

# Install pre-requisites

# Install framework
if PKG=$(curl -sf "http://metadata.google.internal/computeMetadata/v1/instance/attributes/dist" -H "Metadata-Flavor: Google"); then
    # Create work area
    rm -rf $TEMP_DIR
    mkdir $TEMP_DIR
    cd $TEMP_DIR
    
    # Download package
    wget $PKG
    ARTEFACT=`basename $PKG`
    
    # Create target
    mkdir $TARGET_DIR
    tar xvf $ARTEFACT -C $TARGET_DIR/

    # Clean up
    rm -rf $TEMP_DIR
    
fi

echo "*****   Installation Complete!!   *****"

echo "Welcome to IESI!!"

echo "*****   Startup script complete!!    *****"