package hooks;



import driver.DriverManager;
import reporting.ExtentReportManager;
import utils.ConfigManager;
import utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHooks {
    private static final Logger logger = LoggerFactory.getLogger(TestHooks.class);

    @Before
    public void setUp(Scenario scenario) {
        try {
            logger.info("Starting scenario: {}", scenario.getName());

            // Initialize driver if not already done
            if (!DriverManager.isDriverInitialized()) {
                String platform = ConfigManager.getProperty("test.platform", "android");
                DriverManager.initializeDriver(platform);
            }

            // Create extent test
            ExtentReportManager.createTest(scenario.getName(), "Scenario: " + scenario.getName());
            ExtentReportManager.logInfo("Scenario started: " + scenario.getName());

        } catch (Exception e) {
            logger.error("Failed to set up test for scenario: {}", scenario.getName(), e);
            ExtentReportManager.logFail("Failed to set up test: " + e.getMessage());
            throw e;
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            logger.info("Finishing scenario: {} - Status: {}", scenario.getName(), scenario.getStatus());

            // Log scenario result
            if (scenario.isFailed()) {
                ExtentReportManager.logFail("Scenario failed: " + scenario.getName());

                // Capture screenshot for failed scenarios
                try {
                    String screenshotPath = ScreenshotUtils.captureScreenshotForScenario(scenario.getName());
                    if (screenshotPath != null) {
                        // Attach screenshot to Cucumber report
                        byte[] screenshot = ((org.openqa.selenium.TakesScreenshot) DriverManager.getDriver())
                                .getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                        scenario.attach(screenshot, "image/png", "Screenshot");
                    }
                } catch (Exception e) {
                    logger.error("Failed to capture screenshot for failed scenario", e);
                }
            } else {
                ExtentReportManager.logPass("Scenario passed: " + scenario.getName());
            }

        } catch (Exception e) {
            logger.error("Error in tearDown for scenario: {}", scenario.getName(), e);
        } finally {
            // Clean up extent test
            ExtentReportManager.endTest();
        }
    }

    @Before("@android")
    public void setUpAndroid() {
        logger.info("Setting up Android-specific configuration");
        ConfigManager.setProperty("test.platform", "android");
    }

    @Before("@ios")
    public void setUpIOS() {
        logger.info("Setting up iOS-specific configuration");
        ConfigManager.setProperty("test.platform", "ios");
    }

    @After("@cleanup")
    public void cleanupAfterTest() {
        logger.info("Performing cleanup after test");
        // Add any specific cleanup logic here
    }
}
