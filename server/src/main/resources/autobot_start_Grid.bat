@ECHO OFF

set HUB_CONFIG=hubConfig.json
set HUB_TYPE=Grid

if "%1" equ "sauce" (
set HUB_CONFIG=hubSauceConfig.json
set HUB_TYPE=SauceGrid
)

set PATH=%JAVA_HOME%\jre\bin;%JAVA_HOME%\bin;%CD%;%PATH%

start "SeLion %HUB_TYPE% Hub" java -DSeLionConfig=SeLionConfigWindows.json -cp *;. com.paypal.selion.utils.JarSpawner "java -DSeLionConfig=SeLionConfigWindows.json -Djava.util.logging.config.file=logs/logging.properties -cp \"*;.\" com.paypal.selion.grid.SeLionGridLauncher -role hub -hubConfig %HUB_CONFIG%"