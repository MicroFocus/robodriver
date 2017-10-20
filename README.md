# robodriver

> Note: tested with Selenium 3.6.

A WebDriver API to generate native system input events for the purposes of test automation, either locally or on a remote machine.

The `RemoteWebDriver` interface can be used to find screen devices and to implement mouse and keyboard actions:

```java
DesiredCapabilities roboCapabilities = new DesiredCapabilities("robodriver", null, Platform.ANY);
RemoteWebDriver robo = new RemoteWebDriver(roboCapabilities);

// find the default screen,
WebElement screen = robo.findElementByXPath("//screen[@default=true]");

// send keys to screen
screen.sendKeys("hello robodriver");

// click to screen at x,y position
new Actions(robo)
	.moveToElement(screen, 100, 200)
	.click()
	.perform();
```

## Drag & Drop

For drag and drop it is needed to provide a source and target element. For those elements a `//rectangle` of a screen
can be used, defined by x,y coordinates of its left upper corner, width and height can be zero, for example:

> The origin of **robodriver** elements like screens and rectangles is at the top left! 
> This is different to the Selenium W3C Actions, where the origin of a **Browser** WebElement is the center of the element.

```java
WebElement source = screen.findElement(
	By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", xFrom, yFrom)));
WebElement target = screen.findElement(
	By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", xTo, yTo)));
  
new Actions(robo)
	.dragAndDrop(source, target)
	.perform();
```

## Build

Easiest way to build robodriver.jar is using Maven and build file `pom.xml`, install Maven from 
[Apache Maven Project](https://maven.apache.org/)

1. Clone the project.

1. Open a shell window in the folder, usually: `../robodriver`.

1. Extend the environment search path to find chromedriver, it is needed for test runs .

1. Build and run tests with maven command `mvn install`, to skip the test runs you can use `mvn install -DskipTests`.

1. See .jar file in output folder `./target`.

## Tools

The utility `Keyboard` opens a window that logs Selenium key codes and also system dependent virtual key codes 
for the current keyboard in use. 
To start this tool use maven to build robodriver and run `mvn exec:java` from the command line. 
The implementation can be found in class `io.test.automation.robodriver.tools.Keyboard`. 

## Remote Execution

Selenium server can be extended to use robodriver to drive applications on a remote machine.
On server startup the `robodriver.jar` will be loaded by the 
dynamic webdriver loading feature of the Selenium server. 
See also the webdriver provider file `META-INF/services/org.openqa.selenium.remote.server.DriverProvider`  

1. Download Selenium Standalone Server from the [Selenium Project](http://www.seleniumhq.org/download/)

1. Build `robodriver.jar` using Maven build file `pom.xml`, see above.

1. Start server with `robodriver.jar` in the classpath, for example: 
```
java -cp ./robodriver.jar;./selenium-server-standalone-v.v.jar org.openqa.grid.selenium.GridLauncherV3
```

> Note: robodriver.jar must be before Selenium server JAR in the classpath. 
> This is required because of a needed patch to support W3C Actions for robodriver DriverProvider implementation
> and will be obsolete as soon Selenium server supports to configure the needed dialect.
