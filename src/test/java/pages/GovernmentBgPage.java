package pages;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import java.io.File;
import java.io.IOException;
import org.openqa.selenium.WindowType;

public class GovernmentBgPage {

    WebDriver driver;
    WebElement element;
    WebElement elementSection;

    public GovernmentBgPage(WebDriver driver) {
        this.driver = driver;

        element = driver.findElement(By.cssSelector((".header-content .covid19")));
        elementSection = driver.findElement(By.cssSelector((".header-content")));
    }


    public void getScreenshot(String fileName) {
        File screenshotElement = element.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshotElement, new File("./screenshots/" + fileName + ".jpg"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getRect() {
        Rectangle rect = element.getRect();
        System.out.println("Element height is: " + rect.getHeight());
        System.out.println("Element width is: " + rect.getWidth());
        System.out.println("Element X location is: " + rect.getX());
        System.out.println("Element Y location is: " + rect.getY());
        System.out.println("Element dimensions are: " + rect.getDimension());
    }

}
