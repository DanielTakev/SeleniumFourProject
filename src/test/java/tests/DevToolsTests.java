package tests;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.network.model.BlockedReason;
import org.openqa.selenium.devtools.network.model.ConnectionType;
import org.openqa.selenium.devtools.network.model.ResourceType;
import org.openqa.selenium.devtools.security.Security;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import static org.testng.Assert.assertEquals;
import static org.openqa.selenium.devtools.network.Network.*;

public class DevToolsTests {

    protected WebDriver driver;
    protected DevTools devTools;

    @BeforeMethod
    public void setUp() {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        driver.get("https://gov.bg");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @AfterMethod
    public void teardown() {
        // Delete browser session
        // driver.quit();
    }

    // DEV TOOLS TESTS
    // More info at: https://chromedevtools.github.io/devtools-protocol/
    @Test
    public void devToolsNetworkOfflineTest() {

        // Send a request to enable the Network - first parameter is maxTotalBufferSize, the second and the third are optional
        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));
        // Send Network conditions - offline: true, latency: 100, download throughput: 0, upload throughput: 0, connection type: wifi(your connection type)
        devTools.send(Network.emulateNetworkConditions(true, 100, 0, 0, Optional.of(ConnectionType.WIFI)));
        // Add listener to listen for particular response
        devTools.addListener(loadingFailed(), loadingFailed -> assertEquals(loadingFailed.getErrorText(), "net::ERR_INTERNET_DISCONNECTED"));
        devTools.addListener(loadingFailed(), loadingFailed -> System.out.println("The error is: " + loadingFailed.getErrorText()));

        driver.navigate().refresh();
    }

    @Test
    public void devToolsFilterCssPngTest() {
        // Enable Network
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Set blocked URL patterns
        devTools.send(Network.setBlockedURLs(ImmutableList.of("*.css", "*.png")));

        // Add event listener to verify that CSS and PNG are blocked
        devTools.addListener(loadingFailed(), loadingFailed -> {
            if (loadingFailed.getType().equals(ResourceType.STYLESHEET)) {
                Assert.assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }

            else if (loadingFailed.getType().equals(ResourceType.IMAGE)) {
                Assert.assertEquals(loadingFailed.getBlockedReason(), BlockedReason.INSPECTOR);
            }
        });

        driver.navigate().refresh();
    }

    @Test
    public void devToolsLoadInsecureWebsiteTest() {
        // Enable Security
        devTools.send(Security.enable());

        // Set ignore certificate errors
        devTools.send(Security.setIgnoreCertificateErrors(true));

        // Load insecure website
        driver.get("https://expired.badssl.com/");

        // Verify that the page was loaded
        Assert.assertEquals(true, driver.getPageSource().contains("expired"));
    }

    @Test
    public void genericCommandBrowserCloseTest() {
        // Generic browser command to close the browser
        // More info at: https://vanilla.aslushnikov.com/?Browser
        try {
            devTools.send(new Command<>("Browser.close", ImmutableMap.of()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
