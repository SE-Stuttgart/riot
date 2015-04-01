call mvn clean process-classes
if %ERRORLEVEL% NEQ 0 (
   echo Error while compiling the code: %errorlevel%
   exit /b %errorlevel%
)
call ant instrument
if %ERRORLEVEL% NEQ 0 (
   echo Error while instrumenting the code: %errorlevel%
   exit /b %errorlevel%
)
call mvn verify -DskipITs=false -Pcoverage
if %ERRORLEVEL% NEQ 0 (
   echo Error while running the tests: %errorlevel%
   exit /b %errorlevel%
)
call ant report
if %ERRORLEVEL% NEQ 0 (
   echo Error while creating the report: %errorlevel%
   exit /b %errorlevel%
)