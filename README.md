# robodriver
A WebDriver API to generate native system input events for the purposes of test automation, either locally or on a remote machine.

The `RemoteWebDriver` interface can be used to find screen devices and to implement mouse and keyboard actions:

```
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


  