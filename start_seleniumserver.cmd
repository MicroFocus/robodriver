@echo off

echo java executable path:         %1

echo selenium server jar path:     %2

echo gecko driver executable path: %3

start %1 -Dwebdriver.gecko.driver=%3 -cp ./target/classes;%2 org.openqa.grid.selenium.GridLauncherV3 -port 4444

