#!/bin/bash

# Set the window title
export FOLDER_NAME=${PWD##*/}
echo -ne "\033]0;$FOLDER_NAME\007"

build_version() {
    echo
    echo "Starting build for Minecraft $1..."
    ./gradlew build -PtargetVersion=$1
    if [ $? -ne 0 ]; then
        echo
        echo "Build failed for Minecraft $1! Check the error messages above."
        read -p "Press Enter to continue..."
        exit 1
    fi
}

echo "Building mod for all Minecraft versions..."
# ./gradlew clean

build_version "26.1"
build_version "26.1.1"
build_version "26.1.2"




echo
echo "All builds completed successfully!"
echo
echo "You can find the built JARs in the build/libs directory."
read -p "Press Enter to continue..."
