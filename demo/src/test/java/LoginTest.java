import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pages.LoginPage;

public class LoginTest {

    WebDriver driver;
    String url = "https://doaj.org/";

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterClass
    public void tearDown() {
        driver.close();
    }

    @Test(groups = {"fast"})
    public void aFastTest() throws TimeoutException, IOException {
        System.out.println("Test clicking dropdowns");
        driver.get(url);
        LoginPage loginPage = new LoginPage(driver);
        assert loginPage.getLogoText().toLowerCase().contains("the directory of open access journals");
        assert loginPage.getElementText("tagLineText").equals("OPEN\nGLOBAL\nTRUSTED");
        assert loginPage.getElementText("supportDropdownButton").equals("SUPPORT");
        assert loginPage.getElementText("applyDropdownButton").equals("APPLY");
        assert loginPage.getElementText("searchButton").equals("SEARCH");
        assert loginPage.getElementText("loginButton").equals("LOGIN");
        // Test search dropdown button
        loginPage.clickButton("searchDropdownButton");
        loginPage.wait4Element2BeVisible("journalsSearchOption", 10);
        assert loginPage.getElementText("journalsSearchOption").equals("Journals");
        assert loginPage.getElementText("articlesSearchOption").equals("Articles");
        loginPage.elementNotDisplayed("apiDocOption");
        // Test documentation button
        loginPage.clickButton("docsDropdownButton");
        loginPage.wait4Element2NotBeVisible("journalsSearchOption", 5);
        loginPage.wait4Element2BeVisible("apiDocOption", 10);
        assert loginPage.getElementText("apiDocOption").equals("API");
        assert loginPage.getElementText("oaiDocOption").equals("OAI-PMH");
        loginPage.elementNotDisplayed("journalsSearchOption");
        // Test about dropdown button
        loginPage.clickButton("aboutDropdownButton");
        loginPage.wait4Element2BeVisible("aboutAboutOption", 5);
        loginPage.elementNotDisplayed("journalsSearchOption");
        loginPage.elementNotDisplayed("oaiDocOption");
        assert loginPage.getElementText("aboutAboutOption").equals("About DOAJ");
        // Test getting back to no dropdowns open
        loginPage.clickButton("findHeader1");
        loginPage.wait4Element2NotBeVisible("aboutAboutOption", 5);
        loginPage.elementNotDisplayed("journalsSearchOption");
        loginPage.elementNotDisplayed("oaiDocOption");
        loginPage.elementNotDisplayed("aboutAboutOption");
    }

    @Test(groups = {"fast"})
    public void hoveringTest() throws TimeoutException, IOException {
        System.out.println("Test hovering dropdowns");
        driver.get(url);
        LoginPage loginPage = new LoginPage(driver);
        assert loginPage.getLogoText().toLowerCase().contains("the directory of open access journals");
        assert loginPage.getElementText("tagLineText").equals("OPEN\nGLOBAL\nTRUSTED");
        assert loginPage.getElementText("supportDropdownButton").equals("SUPPORT");
        assert loginPage.getElementText("applyDropdownButton").equals("APPLY");
        assert loginPage.getElementText("searchButton").equals("SEARCH");
        assert loginPage.getElementText("loginButton").equals("LOGIN");
        // Test search dropdown button hover
        loginPage.hover2Element("searchDropdownButton");
        loginPage.wait4Element2BeVisible("journalsSearchOption", 10);
        assert loginPage.getElementText("journalsSearchOption").equals("Journals");
        assert loginPage.getElementText("articlesSearchOption").equals("Articles");
        loginPage.elementNotDisplayed("apiDocOption");
        // Test documentation button
        loginPage.hover2Element("docsDropdownButton");
        loginPage.wait4Element2NotBeVisible("journalsSearchOption", 5);
        loginPage.wait4Element2BeVisible("apiDocOption", 10);
        assert loginPage.getElementText("apiDocOption").equals("API");
        assert loginPage.getElementText("oaiDocOption").equals("OAI-PMH");
        loginPage.elementNotDisplayed("journalsSearchOption");
        // Test about dropdown button
        loginPage.hover2Element("aboutDropdownButton");
        loginPage.wait4Element2BeVisible("aboutAboutOption", 5);
        loginPage.elementNotDisplayed("journalsSearchOption");
        loginPage.elementNotDisplayed("oaiDocOption");
        assert loginPage.getElementText("aboutAboutOption").equals("About DOAJ");
        // Test getting back to no dropdowns open
        loginPage.clickButton("findHeader1");
        loginPage.wait4Element2NotBeVisible("aboutAboutOption", 5);
        loginPage.elementNotDisplayed("journalsSearchOption");
        loginPage.elementNotDisplayed("oaiDocOption");
        loginPage.elementNotDisplayed("aboutAboutOption");
    }

    @Test(groups = {"slow"})
    public void aSlowTest() {
        System.out.println("Slow test");
    }

}

