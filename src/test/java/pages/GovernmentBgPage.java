package pages;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GovernmentBgPage {

    protected WebDriver driver;

    public GovernmentBgPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(className = "header-content")
    protected WebElement headerContent;
    @FindBy(css = ".header-content .covid19")
    protected WebElement covidLink;

    // METHODS
    public void getElementScreenshot(String fileName) {
        // Get a screenshot of a Webelement
        File screenshotElement = headerContent.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshotElement, new File("./screenshots/" + fileName + ".jpg"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMyWindowsId(int windowIndex) {
        Set<String> handles = driver.getWindowHandles();
        List<String> myWindowsList = new ArrayList<>(handles);
        return myWindowsList.get(windowIndex);
    }

    public void switchNewBrowserTab(String url) {
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(url);
    }

    public void createNewBrowserWindow(String url) {
        driver.switchTo().newWindow(WindowType.WINDOW);
        driver.get(url);
    }

    public void makeBrowserFullscreen(String url) {
        driver.manage().window().fullscreen();
        driver.get(url);
    }

    public int getElementHeight() {
        // Get the dimensions of a located element
        Rectangle rect = covidLink.getRect();
        // System.out.println("Element height is: " + rect.getHeight());
        // System.out.println("Element width is: " + rect.getWidth());
        // System.out.println("Element X location (horizontal) is: " + rect.getX());
        // System.out.println("Element Y location (vertical) is: " + rect.getY());
        // System.out.println("Element dimensions are: " + rect.getDimension());
        return rect.getHeight();
    }

}
