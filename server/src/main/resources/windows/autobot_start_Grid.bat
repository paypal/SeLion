@ECHO OFF

set currentDir=%CD%
set scriptDir=%~dp0
echo "script dir:%scriptDir%"
cd %scriptDir%
for /D %%I in ("%CD%") do set jarDir=%%~dpI
cd ../
for /D %%I in ("%CD%") do set archiveDir=%%~dpI
cd %currentDir%
 
echo "archive dir:%archiveDir%"
echo "Jar dir:%jarDir%"
echo "Current dir:%currentDir%"
 
set WIN_CONFIG_PREFIX=%archiveDir%/config/windows/
set HUB_CONFIG=%archiveDir%/config/hubConfig.json
set HUB_TYPE=Grid
set CLASS_PATH=%jarDir%/*;.
 
if "%1" equ "sauce" (
set HUB_CONFIG=%archiveDir%/config/hubSauceConfig.json
set HUB_TYPE=SauceGrid
)

if "%1" equ "mobile" (
set HUB_CONFIG=%archiveDir%/config/hubMobileConfig.json
set HUB_TYPE=MobileGrid
set CLASS_PATH=%archiveDir%repository;%CLASS_PATH%
)
 
::Script to dynamically substitute the archive dir path within logging.properties
SETLOCAL ENABLEDELAYEDEXPANSION
SET PROPKEY=java.util.logging.FileHandler.pattern
::The Replace slash is to change \ to / so that JVM treats it as a path instead of escape character
SET REPLACE_SLASH=%archiveDir%
SET PROPVAL=%REPLACE_SLASH:\=/%logs/selion-grid-%%g.log
SET FILE=%archiveDir%config\logging.properties
MOVE /Y "%FILE%" "%FILE%.bak"
FOR /F "USEBACKQ tokens=*" %%A IN (`TYPE "%FILE%.bak" ^|FIND /N /I "%PROPKEY%"`) DO (
  SET LINE=%%A
)
FOR /F "tokens=1 delims=]" %%S in ("%LINE%") DO SET LINE=%%S
SET /A LINE=%LINE:~1%
SET /A COUNT=1
FOR /F "USEBACKQ tokens=*" %%A IN (`FIND /V "" ^<"%FILE%.bak"`) DO (
  IF "!COUNT!" NEQ "%LINE%" (
      ECHO %%A>>"%FILE%"
  ) ELSE (
      ECHO %PROPKEY%=%PROPVAL%>>"%FILE%"
      ECHO Updated %FILE% with value %PROPKEY%=%PROPVAL%
  )
  SET /A COUNT+=1
)

set PATH=%JAVA_HOME%\jre\bin;%JAVA_HOME%\bin;%PATH%

start "SeLion %HUB_TYPE% Hub" java -DarchiveHome=%archiveDir% -DSeLionConfig=%WIN_CONFIG_PREFIX%SeLionConfig.json -cp %CLASS_PATH% com.paypal.selion.utils.JarSpawner "java -DarchiveHome=%archiveDir% -DSeLionConfig=%WIN_CONFIG_PREFIX%SeLionConfig.json -Djava.util.logging.config.file=%archiveDir%/config/logging.properties -cp \"%CLASS_PATH%\" com.paypal.selion.grid.SeLionGridLauncher -role hub -hubConfig %HUB_CONFIG%"
