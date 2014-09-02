
@ECHO OFF

set HUB_CONFIG=hubConfig.json

if "%1" equ "sauce" (
set HUB_CONFIG=hubSauceConfig.json
)

set PATH=%JAVA_HOME%\jre\bin;%JAVA_HOME%\bin;%PATH%

start java -DSeLionConfig=SeLionConfigWindows.json -cp *;. com.paypal.selion.utils.JarSpawner "java -DSeLionConfig=SeLionConfigWindows.json -Djava.util.logging.config.file=logs/logging.properties -cp \"*;.\" com.paypal.selion.grid.SeLionGridLauncher -role hub -hubConfig %HUB_CONFIG%"
