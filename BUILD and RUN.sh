#!/bin/bash

# Set the window title
export FOLDER_NAME=${PWD##*/}
echo -ne "\033]0;$FOLDER_NAME\007"

echo
echo "Starting build for latest Minecraft version..."
./gradlew build
if [ $? -ne 0 ]; then
    echo
    echo "Build failed! Check the error messages above."
    read -p "Press Enter to continue..."
    exit 1
fi

./gradlew runclient
