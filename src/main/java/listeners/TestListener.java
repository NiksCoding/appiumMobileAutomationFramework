package listeners;

import reporting.ExtentReportManager;
import utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

public class TestListener implements ITestListener, ISuiteListener {
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ISuite suite) {
        logger.info("Test suite started: {}", suite.getName());
        ExtentReportManager.logInfo("Test Suite: " + suite.getName() + " started");

        // Set platform from suite parameters
        String platform = suite.getParameter("platform");
        if (platform != null) {
            ConfigManager.setProperty("test.platform", platform);
            ExtentReportManager.logInfo("Platform set to: " + platform);
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.info("Test suite finished: {}", suite.getName());
        ExtentReportManager.logInfo("Test Suite: " + suite.getName() + " finished");
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test started: {}", getTestName(result));
        ExtentReportManager.logInfo("Test Method: " + getTestName(result) + " started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: {}", getTestName(result));
        ExtentReportManager.logPass("Test Method: " + getTestName(result) + " passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: {}", getTestName(result), result.getThrowable());
        ExtentReportManager.logFail("Test Method: " + getTestName(result) + " failed - " +
                result.getThrowable().getMessage());

        // Capture screenshot on failure
        ExtentReportManager.attachScreenshot();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: {}", getTestName(result));
        ExtentReportManager.logSkip("Test Method: " + getTestName(result) + " skipped");

        if (result.getThrowable() != null) {
            ExtentReportManager.logSkip("Skip reason: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test failed but within success percentage: {}", getTestName(result));
        ExtentReportManager.logInfo("Test Method: " + getTestName(result) +
                " failed but within success percentage");
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test context started: {}", context.getName());
        ExtentReportManager.logInfo("Test Context: " + context.getName() + " started");

        // Set test context parameters
        setTestContextParameters(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test context finished: {}", context.getName());
        ExtentReportManager.logInfo("Test Context: " + context.getName() + " finished");

        // Log test statistics
        logTestStatistics(context);
    }

    private String getTestName(ITestResult result) {
        String className = result.getTestClass().getName();
        String methodName = result.getMethod().getMethodName();
        return className + "." + methodName;
    }

    private void setTestContextParameters(ITestContext context) {
        // Set device and platform information from test context
        String platform = context.getCurrentXmlTest().getParameter("platform");
        String deviceName = context.getCurrentXmlTest().getParameter("deviceName");
        String platformVersion = context.getCurrentXmlTest().getParameter("platformVersion");

        if (platform != null) {
            ConfigManager.setProperty("test.platform", platform);
        }
        if (deviceName != null) {
            if ("android".equalsIgnoreCase(platform)) {
                ConfigManager.setProperty("android.device.name", deviceName);
            } else if ("ios".equalsIgnoreCase(platform)) {
                ConfigManager.setProperty("ios.device.name", deviceName);
            }
        }
        if (platformVersion != null) {
            if ("android".equalsIgnoreCase(platform)) {
                ConfigManager.setProperty("android.platform.version", platformVersion);
            } else if ("ios".equalsIgnoreCase(platform)) {
                ConfigManager.setProperty("ios.platform.version", platformVersion);
            }
        }
    }

    private void logTestStatistics(ITestContext context) {
        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();

        String statistics = String.format("Test Statistics - Total: %d, Passed: %d, Failed: %d, Skipped: %d",
                total, passed, failed, skipped);

        logger.info(statistics);
        ExtentReportManager.logInfo(statistics);
    }
}