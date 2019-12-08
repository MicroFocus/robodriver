@echo off
rem download selenium server from https://docs.seleniumhq.org/download/

echo java executable path:          %1

echo Selenium server jar path:      %2

echo chrome driver executable path: %3

echo robodriver class path:         %4

start "selenium-server" %1 -Dwebdriver.chrome.driver=%3 -cp %4;%2 org.openqa.grid.selenium.GridLauncherV3 -port 4444

rem start "selenium-server-debug" %1 -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=y -Dwebdriver.chrome.driver=%3 -ea -cp %4;%2 org.openqa.grid.selenium.GridLauncherV3 -debug -port 4444


