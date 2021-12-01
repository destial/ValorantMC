@echo off
echo Setting JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-16.0.1
echo setting PATH
set PATH=C:\Program Files\Java\jdk-16.0.1\bin;%PATH%
java -Xms128M -Xmx4048M -jar paper.jar -nogui
PAUSE