@echo off


echo Deleting .gradle folder...
rmdir /s /q ".gradle"

echo Deleting build folder...
rmdir /s /q "build"

echo Deleting backup_scr folder...
rmdir /s /q "backup_scr"

echo Cleaning gradlew...
call gradlew clean

echo Updating dependencies...
call gradlew --refresh-dependencies
