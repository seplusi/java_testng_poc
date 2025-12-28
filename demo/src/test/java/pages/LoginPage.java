package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import resources.objects.CommonPage;

public class LoginPage extends CommonPage{

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public String getLogoText() {
        return waitDriver.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty("logoButton").split("\\|")[0]))).getText();
    }
}
