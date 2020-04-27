package tests;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.cachestorage.model.Header;
import org.openqa.selenium.devtools.console.Console;
import org.openqa.selenium.devtools.fetch.Fetch;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.log.Log;
import org.openqa.selenium.devtools.network.model.*;
import org.openqa.selenium.devtools.page.Page;
import org.openqa.selenium.devtools.runtime.Runtime;
import org.openqa.selenium.devtools.security.Security;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.testng.annotations.*;
import pages.GovernmentBgPage;
import static org.junit.Assert.*;
import static org.openqa.selenium.devtools.network.Network.loadingFailed;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
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
    public void getScreenshotTest() {

        String screenshotElementName = "covidScreenshot";
        String screenshotSectionName = "headerContentScreenshot";

        governmentBgPage.getScreenshot(screenshotElementName);
        governmentBgPage.getScreenshot(screenshotSectionName);
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
    }

    @Test
    public void switchNewWindowTest() throws InterruptedException {

        driver.switchTo().newWindow(WindowType.WINDOW);
        Thread.sleep(2000);
        driver.get("https://bing.com");
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
        Thread.sleep(2000);

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

        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));
        devTools.send(Network.emulateNetworkConditions(true, 100, 1000, 2000, Optional.of(ConnectionType.WIFI)));
        devTools.addListener(loadingFailed(), loadingFailed -> assertEquals(loadingFailed.getErrorText(), "net::ERR_INTERNET_DISCONNECTED"));

        Thread.sleep(2000);
        driver.get("https://bing.com");
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