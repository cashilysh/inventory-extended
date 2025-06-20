@echo off
echo Building mod for all Minecraft versions...
::call gradlew clean


echo.
echo Starting build for Minecraft 1.21.5...
call gradlew build -PtargetVersion=1.21.5
if errorlevel 1 goto error

echo.
echo Starting build for Minecraft 1.21.6...
call gradlew build -PtargetVersion=1.21.6
if errorlevel 1 goto error


echo.
echo All builds completed successfully!
goto end

:error
echo.
echo Build failed! Check the error messages above.
pause

:end
echo.
echo You can find the built JARs in the build/libs directory.
pause