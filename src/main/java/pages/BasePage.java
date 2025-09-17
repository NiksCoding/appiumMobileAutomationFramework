package pages;

import driver.DriverManager;
import reporting.ExtentReportManager;
import utils.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class BasePage {
    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected AppiumDriver driver;
    protected WebDriverWait wait;

    protected static final int DEFAULT_TIMEOUT = ConfigManager.getIntProperty("test.timeout.explicit", 10);

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    /**
     * Wait for element to be visible
     */
    protected WebElement waitForElementToBeVisible(WebElement element) {
        try {
            WebElement visibleElement = wait.until(ExpectedConditions.visibilityOf(element));
            ExtentReportManager.logInfo("Element is visible: " + element.toString());
            return visibleElement;
        } catch (Exception e) {
            ExtentReportManager.logFail("Element not visible: " + element.toString());
            logger.error("Element not visible", e);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable
     */
    protected WebElement waitForElementToBeClickable(WebElement element) {
        try {
            WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(element));
            ExtentReportManager.logInfo("Element is clickable: " + element.toString());
            return clickableElement;
        } catch (Exception e) {
            ExtentReportManager.logFail("Element not clickable: " + element.toString());
            logger.error("Element not clickable", e);
            throw e;
        }
    }

    /**
     * Click element with wait
     */
    protected void clickElement(WebElement element, String elementName) {
        try {
            waitForElementToBeClickable(element).click();
            ExtentReportManager.logPass("Clicked on: " + elementName);
            logger.info("Clicked on element: {}", elementName);
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to click on: " + elementName + " - " + e.getMessage());
            logger.error("Failed to click on element: {}", elementName, e);
            throw e;
        }
    }

    /**
     * Send text to element
     */
    protected void sendTextToElement(WebElement element, String text, String elementName) {
        try {
            waitForElementToBeVisible(element).clear();
            element.sendKeys(text);
            ExtentReportManager.logPass("Entered text '" + text + "' in: " + elementName);
            logger.info("Entered text '{}' in element: {}", text, elementName);
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to enter text in: " + elementName + " - " + e.getMessage());
            logger.error("Failed to enter text in element: {}", elementName, e);
            throw e;
        }
    }

    /**
     * Get text from element
     */
    protected String getTextFromElement(WebElement element, String elementName) {
        try {
            String text = waitForElementToBeVisible(element).getText();
            ExtentReportManager.logInfo("Got text '" + text + "' from: " + elementName);
            logger.info("Got text '{}' from element: {}", text, elementName);
            return text;
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to get text from: " + elementName + " - " + e.getMessage());
            logger.error("Failed to get text from element: {}", elementName, e);
            throw e;
        }
    }

    /**
     * Check if element is displayed
     */
    protected boolean isElementDisplayed(WebElement element, String elementName) {
        try {
            boolean isDisplayed = element.isDisplayed();
            ExtentReportManager.logInfo("Element '" + elementName + "' display status: " + isDisplayed);
            logger.info("Element '{}' display status: {}", elementName, isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            ExtentReportManager.logInfo("Element '" + elementName + "' is not displayed");
            logger.info("Element '{}' is not displayed", elementName);
            return false;
        }
    }

    /**
     * Scroll to element (platform agnostic)
     */
    protected void scrollToElement(WebElement element, String elementName) {
        try {
            // Use JavaScript executor for scrolling
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            ExtentReportManager.logInfo("Scrolled to element: " + elementName);
            logger.info("Scrolled to element: {}", elementName);
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to scroll to element: " + elementName + " - " + e.getMessage());
            logger.error("Failed to scroll to element: {}", elementName, e);
            throw e;
        }
    }

    /**
     * Wait for element to disappear
     */
    protected void waitForElementToDisappear(WebElement element, String elementName) {
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            ExtentReportManager.logInfo("Element disappeared: " + elementName);
            logger.info("Element disappeared: {}", elementName);
        } catch (Exception e) {
            ExtentReportManager.logFail("Element did not disappear: " + elementName + " - " + e.getMessage());
            logger.error("Element did not disappear: {}", elementName, e);
            throw e;
        }
    }

    /**
     * Get page title or app name
     */
    public String getPageTitle() {
        try {
            // For mobile apps, this might return the app name or current activity
            String title = driver.getTitle();
            if (title == null || title.isEmpty()) {
                title = driver.getCurrentUrl(); // Fallback
            }
            ExtentReportManager.logInfo("Current page title: " + title);
            return title;
        } catch (Exception e) {
            logger.error("Failed to get page title", e);
            return "Unknown";
        }
    }

    /**
     * Navigate back
     */
    public void navigateBack() {
        try {
            driver.navigate().back();
            ExtentReportManager.logInfo("Navigated back");
            logger.info("Navigated back");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to navigate back: " + e.getMessage());
            logger.error("Failed to navigate back", e);
            throw e;
        }
    }

    /**
     * Refresh/reload current view
     */
    public void refresh() {
        try {
            driver.navigate().refresh();
            ExtentReportManager.logInfo("Page refreshed");
            logger.info("Page refreshed");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to refresh page: " + e.getMessage());
            logger.error("Failed to refresh page", e);
            throw e;
        }
    }
}
