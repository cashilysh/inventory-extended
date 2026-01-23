#!/bin/bash

# Set the window title
export FOLDER_NAME=${PWD##*/}
echo -ne "\033]0;$FOLDER_NAME\007"

echo "Deleting .gradle folder..."
rm -rf ".gradle"

echo "Deleting build folder..."
rm -rf "build"

echo "Deleting backup_scr folder..."
rm -rf "backup_scr"

echo "Cleaning gradlew..."
./gradlew clean

echo "Updating version mappings..."
./gradlew updateversionmappings

echo "Updating dependencies..."
./gradlew --refresh-dependencies
