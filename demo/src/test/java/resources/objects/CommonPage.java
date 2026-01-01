package resources.objects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CommonPage {
    public WebDriver driver;
    public WebDriverWait waitDriver;
    public Properties prop;
    public int timeout;

    public CommonPage(WebDriver driver) {
        this.driver = driver;
        waitDriver = new WebDriverWait(this.driver, Duration.ofSeconds(20));
        prop = loadProperties();
        waitForElementsToLoad(prop, waitDriver);
        timeout = 20;
    }
    
    private void waitForElementsToLoad(Properties prop, WebDriverWait waitDriver) {
        // Method that reads all selectors from page object selectors file and assures the ones
        // that should be present in the beginning are loaded
        String valuesList[];
        
        for (Map.Entry<Object, Object> entry : prop.entrySet()) {
            valuesList = (String[]) ((String) entry.getValue()).split("\\|");
            if ( valuesList[valuesList.length -1].strip().equals("load") ) {
                waitDriver.ignoring(StaleElementReferenceException.class).until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(valuesList[0])));
            }
        }
    }

    private Properties loadProperties() {
        // Loads all selectors from page object selector's file and puts then in a properties object
        String fileName;

        prop = new Properties();
        fileName = "src/test/java/resources/config/".concat(this.getClass().getSimpleName().toLowerCase()).concat("_selectors.txt");
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
                System.out.println("aaaa");
        } catch (IOException ex) {
            System.out.println("ghjk");
        }

        return prop;
    }

    public void clickButton(String buttonElementName) {
        waitDriver.ignoring(StaleElementReferenceException.class).until(
            ExpectedConditions.elementToBeClickable(By.cssSelector(prop.getProperty(buttonElementName).split("\\|")[0]))).click();
    }

    public String getElementText(String elementName) {
        return waitDriver.ignoring(StaleElementReferenceException.class).until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty(elementName).split("\\|")[0]))).getText();
    }

    public String getElementAttr(String elementName, String attr) {
        return waitDriver.ignoring(StaleElementReferenceException.class).until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty(elementName).split("\\|")[0]))).getAttribute(attr);
    }

    public WebElement wait4Element2BeVisible(String elementName, int timeout) throws TimeoutException {
        // Waits for a specific element to be visible in the DOM
        WebElement element = null;
        long initialTs = Instant.now().getEpochSecond();

        while (Instant.now().getEpochSecond() <= initialTs + timeout) {
            try {
                element = driver.findElement(By.cssSelector(prop.getProperty(elementName).split("\\|")[0]));
                if (element.isDisplayed()) { break; }
                else { element = null; }
            } catch (Exception e) {
                sleep(500);
            }
        }
        if (element != null) { return element; }
        else { throw new TimeoutException("test failed"); }
    }

    public void wait4Element2NotBeVisible(String elementName, int timeout) throws TimeoutException {
        // Waits for an element not to be visible in the DOM
        long initialTs = Instant.now().getEpochSecond();

        while (Instant.now().getEpochSecond() <= initialTs + timeout) {
            try {
                assert driver.findElement(By.cssSelector(prop.getProperty(elementName).split("\\|")[0])).isDisplayed();
                sleep(500);
            } catch (NoSuchElementException | AssertionError e) {
                // The element no longer exists. Bailout
                return;
            }
        }
        throw new TimeoutException("Element " + elementName + " still visible.");
    }

    public void elementNotDisplayed(String elementName) {
        // Waits for an element not to be visible in the DOM
        waitDriver.ignoring(StaleElementReferenceException.class).until(
            ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(prop.getProperty(elementName).split("\\|")[0]))
        );
    }

    public void hover2Element(String elementToHover) {
        Actions actions = new Actions(driver);
        WebElement element = waitDriver.ignoring(StaleElementReferenceException.class).until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty(elementToHover).split("\\|")[0]))
        );
        actions.moveToElement(element).perform();
    }

    public void scroll2Element(String elementText) {
        int attempt = 0;
        while ( attempt < 2) {
            try {
                Actions actions = new Actions(driver);
                WebElement element = waitDriver.ignoring(StaleElementReferenceException.class).until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty(elementText).split("\\|")[0]))
                );
                actions.scrollToElement(element).perform();
                break;
            } catch (StaleElementReferenceException e) {
                System.out.println("Caught exception in scrollToElement");
                attempt = attempt + 1;
            }
        }

    }

    public static void sleep(int time2sleep) {
        try {
            // Pause the current thread for 500 milliseconds (half a second)
            Thread.sleep(time2sleep);
        } catch (InterruptedException e) {
            // This exception is thrown if another thread interrupts the current thread
            // while it is sleeping, waiting, or otherwise occupied.
            System.err.println("The thread was interrupted during sleep!");
            // It is often good practice to restore the interrupt flag
            Thread.currentThread().interrupt();
        }
    }

    public void sendText2Element(String element, String text) {
        String elementSelector = prop.getProperty(element).split("\\|")[0];
        WebElement webElement = waitDriver.ignoring(StaleElementReferenceException.class).until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(elementSelector))
        );
        webElement.sendKeys(text);
    }

    public void wait4NumberElementsVisible(String element, int numEle) {
        long initialTs = Instant.now().getEpochSecond();
        while ( Instant.now().getEpochSecond() < initialTs + timeout) {
            List<WebElement> listElements = waitDriver.ignoring(StaleElementReferenceException.class).until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(prop.getProperty(element).split("\\|")[0]))
            );
            if ( listElements.size() == numEle ) { return; }
        }
        throw new NotFoundException("Did not find " + numEle + " of element " + element);
    }
}
