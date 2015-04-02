call mvn clean install -Pcoverage -pl '-maven-plugin,-webapp,-simulation-client,-android' -DskipITs=false
if %ERRORLEVEL% NEQ 0 (
   echo Error while compiling the code: %errorlevel%
   exit /b %errorlevel%
)
call ant report
if %ERRORLEVEL% NEQ 0 (
   echo Error while creating the report: %errorlevel%
   exit /b %errorlevel%
)