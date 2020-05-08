package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.locators.RelativeLocator;
import static org.openqa.selenium.support.locators.RelativeLocator.withTagName;

public class ScaleFocusHomePage {

    protected WebDriver driver;

    public ScaleFocusHomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // SELECTORS
    @FindBy(css = ".icons-menu .icon-share.open-share")
    protected WebElement shareNavigationIcon;
    @FindBy(className = "search-container")
    protected WebElement searchContainer;
    @FindBy(css = ".gtm-hp-vmbtn-serv.view-more-btn")
    protected WebElement servicesViewMoreButton;
    @FindBy(css = ".desciprtion .h3-p")
    protected WebElement descriptionServicesParagraph;

    // METHODS
    public void clickSearchIconFromNavigation() {
        // Click to right of Careers in the Navigation drop down menu
        driver.findElement(RelativeLocator.withTagName("i")
                .toLeftOf(shareNavigationIcon))
                .click();
    }

    public String getSearchContainerAttribute(String attribute) {
        // Return element's passed attribute
        return searchContainer.getAttribute(attribute);
    }

    public void clickCloudServicesViewMoreButton() {
        // To remove "RelativeLocator." use: import static org.openqa.selenium.support.locators.RelativeLocator.withTagName;
        driver.findElement(withTagName("span")
                .toRightOf(servicesViewMoreButton)
                .below(descriptionServicesParagraph))
                .click();
    }
}