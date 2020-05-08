package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ScaleFocusHomePage;
import java.util.concurrent.TimeUnit;

public class ScaleFocusTests {

    protected WebDriver driver;
    protected ScaleFocusHomePage scaleFocusHomePage;

    @BeforeMethod
    public void setUp() {
        // Create browser session
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        scaleFocusHomePage = new ScaleFocusHomePage(driver);

        driver.get("https://www.scalefocus.com/");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @AfterMethod
    public void teardown() {
        // Delete browser session
        driver.quit();
    }

    // RELATIVE LOCATORS
    @Test
    public void pressCloudServicesViewMoreButtonTest() {
        // Scroll down 630 px to thumbnail services
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("window.scrollBy(0,630)", "");
        // Click Cloud Services "View More" button via relative locator
        scaleFocusHomePage.clickCloudServicesViewMoreButton();
        String pageTitle = driver.getTitle();
        System.out.println("The title of the page is: " + pageTitle);

        Assert.assertEquals(pageTitle,"Cloud Services | ScaleFocus");
    }

    @Test
    public void clickSearchNavigationIconTest() throws InterruptedException {
        // Click "Search icon" from navigation menu using relative locator
        scaleFocusHomePage.clickSearchIconFromNavigation();
        String attribute = "style";
        String actualAttribute = scaleFocusHomePage.getSearchContainerAttribute(attribute);
        String notExpectedAttribute = "display: none;";
        System.out.println("The value of attribute \"" + attribute + "\" is: " + actualAttribute);
        Assert.assertNotEquals(actualAttribute, notExpectedAttribute);
    }
}
