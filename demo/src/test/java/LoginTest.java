import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import pages.LoginPage;

public class LoginTest {

    WebDriver driver;
    String url = "https://opensource-demo.orangehrmlive.com/";

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
    public void aFastTest() {
        System.out.println("Fast test");
        driver.get(url);
        LoginPage loginPage = new LoginPage(driver);
        assert loginPage.getTitleText().equals("Login");
        System.out.println("Finished test");
    }

    @Test(groups = {"slow"})
    public void aSlowTest() {
        System.out.println("Slow test");
    }

}

