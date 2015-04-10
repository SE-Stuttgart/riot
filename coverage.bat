call mvn clean install -PcoverageCollect -pl '-maven-plugin,-webapp,-simulation-client,-android' -DskipITs=false
if %ERRORLEVEL% NEQ 0 (
   echo Error while compiling the code: %errorlevel%
   exit /b %errorlevel%
)
call mvn test -PcoverageReport --non-recursive
if %ERRORLEVEL% NEQ 0 (
   echo Error while creating the report: %errorlevel%
   exit /b %errorlevel%
)