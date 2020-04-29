package tests;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.network.model.*;
import org.openqa.selenium.devtools.security.Security;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.testng.annotations.*;
import pages.GovernmentBgPage;
import static org.junit.Assert.*;
import static org.openqa.selenium.devtools.network.Network.loadingFailed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class Tests {

    WebDriver driver;
    GovernmentBgPage governmentBgPage;
    DevTools devTools;

    public Tests() {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.get("https://www.gov.bg/");

        governmentBgPage = new GovernmentBgPage(driver);

        devTools = ((ChromeDriver)driver).getDevTools();
        devTools.createSession();
    }

    @AfterClass
    public void teardown() {
        driver.quit();
    }

    @Test
    public void getScreenshotTest() throws InterruptedException {

        String screenshotElementName = "covidScreenshot";
        String screenshotSectionName = "headerContentScreenshot";

        governmentBgPage.getScreenshot(screenshotElementName);
        governmentBgPage.getScreenshot(screenshotSectionName);
        Thread.sleep(2000);
    }

    @Test
    public void windowFullScreenTest() throws InterruptedException {

        driver.manage().window().fullscreen();
        Thread.sleep(2000);
    }

    @Test
    public void switchNewWindowTabTest() throws InterruptedException {

        driver.switchTo().newWindow(WindowType.TAB);
        Thread.sleep(2000);
        driver.get("https://google.com");
        Thread.sleep(2000);
        driver.close();
        Thread.sleep(1000);
    }

    @Test
    public void switchNewWindowTest() throws InterruptedException {

        System.out.println("Before switching the title is: " + driver.getTitle());
        Thread.sleep(1000);
        driver.switchTo().newWindow(WindowType.WINDOW);

        Set<String> handles = driver.getWindowHandles();
        List<String> myWindowsList = new ArrayList<>(handles);

        String parentWindowId = myWindowsList.get(0);
        String childWindowId = myWindowsList.get(1);

        System.out.println(parentWindowId);
        System.out.println(childWindowId);

        Thread.sleep(2000);
        driver.get("https://bing.com");

        Dimension dimension = new Dimension(800,480);
        driver.manage().window().setSize(dimension);
        Thread.sleep(3000);

        System.out.println("After switching the title of the new browser window is: " + driver.getTitle());

        Thread.sleep(1000);
        driver.switchTo().window(parentWindowId);

        System.out.println("After second switching the title is: " + driver.getTitle());
        Thread.sleep(2000);
    }

    @Test
    public void getRectTest() {

        governmentBgPage.getRect();
    }

    // RELATIVE LOCATORS
    @Test
    public void relativeLocatorsTest() throws InterruptedException {

        driver.findElement(RelativeLocator.withTagName("a")
                .toLeftOf(By.className("language"))
                .toRightOf(By.className("right")))
                .click();
        Thread.sleep(5000);
    }

    @Test
    public void relativeLocatorsScaleFocusTest() throws InterruptedException {

        driver.get("https://www.scalefocus.com/");
        Thread.sleep(3000);

        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("window.scrollBy(0,630)", "");
        Thread.sleep(3000);

        driver.findElement(RelativeLocator.withTagName("span")
                .toRightOf(By.cssSelector(".view-more-btn.gtm-hp-vmbtn-serv"))
                .below(By.cssSelector(".desciprtion .h3-p")))
                .click();
        Thread.sleep(5000);
    }

    // DEV TOOLS TESTS
    // More info at: https://chromedevtools.github.io/devtools-protocol/
    @Test
    public void devToolsNetworkOfflineTest() throws InterruptedException {

        // Send a request to enable the Network - first parameter is maxTotalBufferSize, the second and the third are optional
        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));
        // Send Network conditions - offline: true, latency: 100, download throughput: 1000, upload throughput: 2000, connection type: wifi(your connection type)
        devTools.send(Network.emulateNetworkConditions(true, 100, 0, 0, Optional.of(ConnectionType.WIFI)));
        // Add listener to listen for particular response
        devTools.addListener(loadingFailed(), loadingFailed -> assertEquals(loadingFailed.getErrorText(), "net::ERR_INTERNET_DISCONNECTED"));
        Thread.sleep(2000);

        driver.get("https://bing.com");
        Thread.sleep(2000);
    }

    @Test
    public void devToolsFilterUrls() throws InterruptedException {

        // Enable Network
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Set blocked URL patterns
        devTools.send(Network.setBlockedURLs(ImmutableList.of("*.css", "*.png")));

        // Add event listener to verify that css and png are blocked
        devTools.addListener(loadingFailed(), loadingFailed -> {

            if (loadingFailed.getType().equals(ResourceType.STYLESHEET)) {
                Assert.assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }

            else if (loadingFailed.getType().equals(ResourceType.IMAGE)) {
                Assert.assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }
        });

        driver.get("https://apache.org");
        Thread.sleep(4000);
    }

    @Test
    public void devToolsLoadInsecureWebsite() {

        // Enable Security
        devTools.send(Security.enable());

        // Set ignore certificate errors
        devTools.send(Security.setIgnoreCertificateErrors(true));

        //load insecure website
        driver.get("https://expired.badssl.com/");

        // Verify that the page was loaded
        Assert.assertEquals(true, driver.getPageSource().contains("expired"));
    }

    @Test
    public void genericGetBrowserVersion() {

        // More info at: https://vanilla.aslushnikov.com/?Browser
        devTools.send(new Command<>("Browser.getVersion", ImmutableMap.of()));
    }
}