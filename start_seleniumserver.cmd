@echo off

echo java executable path:               %1

echo selenium server jar path:           %2

echo chrome driver executable path:      %3

echo robodriver class path or .jar file: %4

start %1 -Dwebdriver.chrome.driver=%3 -cp %4;%2 org.openqa.grid.selenium.GridLauncherV3 -port 4444

