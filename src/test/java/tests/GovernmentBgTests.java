package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.GovernmentBgPage;
import java.util.concurrent.TimeUnit;

public class GovernmentBgTests {

    protected WebDriver driver;
    protected GovernmentBgPage governmentBgPage;
    String googleUrl = "https://google.com";

    @BeforeMethod
    public void setUp() {
        // Create a new browser session
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        governmentBgPage = new GovernmentBgPage(driver);

        driver.get("https://gov.bg");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }

    @AfterMethod
    public void teardown() {
        // Delete browser session
        driver.quit();
    }

    @Test
    public void getElementScreenshotTest() {

        String fileName = "Header_Content_Screenshot";
        governmentBgPage.getElementScreenshot(fileName);
    }

    @Test
    public void switchNewBrowserTabTest() {
        // Switch to new browser tab
        System.out.println("The title of the current browser tab is: " + driver.getTitle());

        governmentBgPage.switchNewBrowserTab(googleUrl);
        String newTabTitle = driver.getTitle();

        System.out.println("The title of the new browser tab is: " + newTabTitle);
        Assert.assertEquals("Google", newTabTitle);
    }

    @Test
    public void switchNewWindowTest() {
        // Switch to new browser window
        String parentWindowTitle = driver.getTitle();
        String parentWindowId = governmentBgPage.getMyWindowsId(0);
        System.out.println("Parent window title is: " + parentWindowTitle);
        System.out.println("Parent window ID is: " + parentWindowId);

        governmentBgPage.createNewBrowserWindow(googleUrl);
        String newWindowTitle = driver.getTitle();
        System.out.println("New window title is: " + newWindowTitle);

        try {
            String newWindowId = governmentBgPage.getMyWindowsId(1);
            System.out.println("New window ID is: " + newWindowId);
        } catch (Exception e) {
            System.out.println("There is no such index!");
        }

        Assert.assertNotEquals(parentWindowId, newWindowTitle);
    }

    @Test
    public void browserFullScreenTest() {
        // Open the browser in fullscreen mode
        governmentBgPage.makeBrowserFullscreen(googleUrl);
    }

    @Test
    public void assertHeightOfElementTest() {
        // Get the height of the located element
        Assert.assertEquals(50, governmentBgPage.getElementHeight());
    }
}
