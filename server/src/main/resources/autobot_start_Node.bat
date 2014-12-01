@ECHO OFF

set PATH=%JAVA_HOME%\jre\bin;%JAVA_HOME%\bin;%CD%;%PATH%
set HUB_URL=http://127.0.0.1:4444/grid/register
if exist selenium*.jar (del selenium*.jar)
start "SeLion Grid Node connected to %HUB_URL%" java -DSeLionConfig=SeLionConfigWindows.json -cp *;. com.paypal.selion.utils.JarSpawner  "java -DSeLionConfig=SeLionConfigWindows.json -Djava.util.logging.config.file=logs/logging.properties -cp \"*;.\" com.paypal.selion.grid.SeLionGridLauncher -role node -hub %HUB_URL% -port 5556 -nodeConfig nodeConfigWindows.json -browserTimeout 60 -servlets com.paypal.selion.node.servlets.NodeAutoUpgradeServlet,com.paypal.selion.node.servlets.NodeForceRestartServlet,com.paypal.selion.node.servlets.LogServlet"