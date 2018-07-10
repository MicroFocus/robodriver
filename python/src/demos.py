'''

The examples are using a Web browser for robodriver demos.

Ensure Selenium Server is running, modify server endpoint if needed (see below), default = 4444.

'''

import tempfile

from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains


def demos():

    server_endpoint = "http://127.0.0.1:4444/wd/hub"

    browser = webdriver.Remote(server_endpoint, DesiredCapabilities.CHROME)
    browser.get('file:///C:/Users/geri/Documents/GitHub/robodriver/target/test-classes/test.html')
    
    # connect robodriver to the Selenium server
    robo_capabilities={
        'browserName': 'io.test.automation.robodriver', 
        'platform':    'ANY' }
    robo = webdriver.Remote(server_endpoint, robo_capabilities)
   
    def action_type_keys():
        elem = browser.find_element_by_id('outputs');
        elem.click() # set focus to input field
        
        print("Type upper case keys using Selenium action API.")
        
        # type 'HELLO' to the default screen by sending native keyboard events
        ActionChains(robo)         \
            .key_down(Keys.SHIFT)  \
            .send_keys('hello')    \
            .key_up(Keys.SHIFT)    \
            .perform()
            
    def capture_screen_clipping():
        print("Capture screen clipping.")
        
        # find screen rectangle by: dim=x,y,width,height
        clipping = robo.find_element_by_xpath(
            "//screen[@default=true]//rectangle[@dim='{},{},{},{}']".format(50,100,300,200))
        
        # capture clipping image to file
        image_file_name = "{}/robodriver_demo.png".format(tempfile.gettempdir())
        clipping.screenshot(image_file_name)
        print("write captured screen clipping image file to: {}".format(image_file_name))

    action_type_keys()
    capture_screen_clipping()
    
demos()

if __name__ == '__main__':
    pass