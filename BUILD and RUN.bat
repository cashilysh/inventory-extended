@echo off

echo.
echo Starting clean build...
call gradlew clean build
if errorlevel 1 goto error


call gradlew runclient

:error
echo.
echo Build failed! Check the error messages above.
pause