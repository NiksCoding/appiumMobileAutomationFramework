package runners;

import driver.DriverManager;
import reporting.ExtentReportManager;
import utils.ConfigManager;
import utils.ScreenshotUtils;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions", "hooks"},
        plugin = {
                "pretty",
                "html:test-output/cucumber-reports/Cucumber.html",
                "json:test-output/cucumber-reports/Cucumber.json",
                "junit:test-output/cucumber-reports/Cucumber.xml",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        tags = "@login ",
        monochrome = true,
        publish = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("Starting test suite execution");

        // Initialize ExtentReports
        ExtentReportManager.createInstance();
        ExtentReportManager.logInfo("Test Suite Started");
        logger.info("Test suite started");
        // Clean up old screenshots (keep last 7 days)
        ScreenshotUtils.cleanupOldScreenshots(7);

        // Set system properties for parallel execution
        System.setProperty("dataproviderthreadcount", "2");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("Finishing test suite execution");

        // Quit driver if still active
        if (DriverManager.isDriverInitialized()) {
            DriverManager.quitDriver();
        }

        // Generate final report
        ExtentReportManager.flush();

        logger.info("Test suite execution completed");
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        logger.debug("Before method execution");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        logger.debug("After method execution");

        // Ensure driver is quit after each test method for parallel execution
        if (DriverManager.isDriverInitialized()) {
            DriverManager.quitDriver();
        }
    }

    /**
     * Enable parallel execution at scenario level
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    /**
     * Get current platform for test execution
     */
    public static String getCurrentPlatform() {
        return ConfigManager.getProperty("test.platform", "android");
    }

    /**
     * Set platform for test execution
     */
    public static void setPlatform(String platform) {
        ConfigManager.setProperty("test.platform", platform);
        logger.info("Platform set to: {}", platform);
    }
}
