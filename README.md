# robodriver

> Note: requires Selenium 3.12.0 client and server.

A platform independent WebDriver API implementation for native I/O control, either locally or on a remote machine:

* Screenshots
* Mouse moves
* Drag and Drop
* Keyboard inputs
* Remoting: Selenium server/grid support
* Java, Python, C#, JavaScript, Ruby

```java
RemoteWebDriver robo = new RoboDriver();

// find the default screen,
WebElement screen = robo.findElementByXPath("//screen[@default=true]");

// type keys 
screen.sendKeys("hello robodriver");

// click to the screen at x,y position
new Actions(robo)
	.moveToElement(screen, 100, 200)
	.click()
	.perform();
```

## Python, JavaScript, Ruby...

Any language binding can be used when running robodriver with Selenium server, see also chapter Remote Execution below. 

Example in Python:

```python
from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains

server_endpoint = "http://127.0.0.1:4444/wd/hub"

# connect robodriver to the Selenium server
robo_capabilities={
    'browserName': 'io.test.automation.robodriver', 
    'platform':    'ANY' }
robo = webdriver.Remote(server_endpoint, robo_capabilities)

# type 'HELLO' to the default screen by sending native keyboard events
ActionChains(robo)         \
    .key_down(Keys.SHIFT)  \
    .send_keys('hello')    \
    .key_up(Keys.SHIFT)    \
    .perform()
```

## Drag & Drop

For drag and drop it is needed to provide a source and target element. For those elements a `//rectangle` of a screen
can be used, defined by x,y coordinates of its left upper corner, width and height can be zero, for example:

> Note: The origin of **robodriver** `WebElement` objects like screens and rectangles is at the **top left!** 
> This is different to Selenium DOM elements and W3C Actions, where the origin of a **Browser** `WebElement` is the center of the element.

```java
WebElement source = screen.findElement(
	By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", xFrom, yFrom)));
WebElement target = screen.findElement(
	By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", xTo, yTo)));
  
new Actions(robo)
	.dragAndDrop(source, target)
	.perform();
```

## Capture Screenshot or Rectangle

To capture full screen or rectanlge areas the screen and rectangle element objects can be used:

```java
// get screenshot from default monitor
File screenshotFile = robo.getScreenshotAs(OutputType.FILE);

// get screenshot from a spedific monitor
WebElement screen = robo.findElementByXPath("/screen[0]");
File screenshotFile = screen.getScreenshotAs(OutputType.FILE);

// capture a specific area from the default screen,
// at pixel position 50,100 (from left upper corner) and width = 300, height = 500
WebElement screenRectangle = robo.findElementByXPath(
    "//screen[@default=true]//rectangle[@dim='50, 100, 300, 500']");
File screenRectangleFile = screenRectangle.getScreenshotAs(OutputType.FILE);
```

## Build

Easiest way to build robodriver.jar is using Maven and build file `pom.xml`, install Maven from 
[Apache Maven Project](https://maven.apache.org/)

1. Clone the project.

1. Open a shell window in the folder, usually: `../robodriver`.

1. Ensure Chrome is installed and extend the environment search path to find latest chromedriver binary, it is needed for test runs .

1. Build and run tests with maven command `mvn install`, to skip the test runs you can use `mvn install -DskipTests`.

1. See robodriver.jar file in output folder `./target`.

## Tools

The utility `Keyboard` opens a window that logs Selenium key codes and also system dependent virtual key codes 
for the current keyboard in use. 
To start this tool use maven to build robodriver and run `mvn exec:java` from the command line.
The Selenium class `Keys` do not support all virtual keys of a specific keyboard, for example to type a grave or acute 
the virtual key code constants can be used. Use `Keyboard` to find the specific VK-IDs or see VK_xxx constants of 
[KeyEvent javadoc](https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html).
   
Example: `Keyboard` outputs typing a latin capital letter A with acute
```
Keys.SHIFT           VK_SHIFT                       (key=Shift, char='￿', ext-code=0x10)
Keys.<NO VK>         VK_DEAD_ACUTE                  (key=Dead Grave, char='`', ext-code=0x81)
Keys.<NO VK>         VK_A                           (key=A, char='A', ext-code=0x41)
```

In your script you can use the VK_xxx constant names, they are interpreted by robodriver on replay:
```java
new Actions(robo)
	.keyDown(Keys.SHIFT)
	.perform();
screen.sendKeys("VK_DEAD_ACUTE", "A"); 
new Actions(robo)
	.keyUp(Keys.SHIFT)
	.perform();
```

The implementation can be found in class `io.test.automation.robodriver.tools.Keyboard`. 


## Remote Execution

Selenium server can be extended to use robodriver to drive applications on a remote machine.
With that robodriver becomes client binding language agnostic. 
On server startup the `robodriver.jar` will be loaded by the 
dynamic webdriver loading feature of the Selenium server. 
See also the webdriver provider file `META-INF/services/org.openqa.selenium.remote.server.DriverProvider`  

1. Download Selenium Standalone Server from the [Selenium Project](http://www.seleniumhq.org/download/)

1. Build `robodriver.jar` using Maven build file `pom.xml`, see above.

1. Start server with `robodriver.jar` in the classpath, for example: 
```
java -cp ./robodriver.jar;./selenium-server-standalone-v.v.jar org.openqa.grid.selenium.GridLauncherV3
```

> Note: `robodriver.jar` must be before the Selenium server JAR in the classpath. 
> This is required because of a needed patch to support W3C Actions protocol for the robodrivers `DriverProvider` implementation
> and will be obsolete as soon Selenium server supports to configure the needed dialect. For the patched code see classes in package`org.openqa.selenium...`.

Portable example in Java:

```java
URL serverEndpoint = new URL("http://localhost:4444/wd/hub");

DesiredCapabilities roboCapabilities = new DesiredCapabilities();
roboCapabilities.setCapability("browserName", "io.test.automation.robodriver");
roboCapabilities.setCapability("platform", "ANY");

RemoteWebDriver robodriver = new RemoteWebDriver(serverEndpoint, roboCapabilities);
```
