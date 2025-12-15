package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage {
    WebDriver driver;
    WebDriverWait waitDriver;
    WebElement titleElement;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.waitDriver = new WebDriverWait(driver, Duration.ofSeconds(20));

        titleElement = waitDriver.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h5")));
    }

    public String getTitleText() {
        return titleElement.getText();
    }


}
