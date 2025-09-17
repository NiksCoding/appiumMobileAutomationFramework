package reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import utils.ConfigManager;
import utils.ScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportManager {
    private static final Logger logger = LoggerFactory.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final String REPORT_PATH = ConfigManager.getProperty("report.path", "test-output/ExtentReport.html");

    public static synchronized ExtentReports createInstance() {
        if (extent == null) {
            String reportPath = getReportPath();
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            sparkReporter.config().setTheme(getTheme());
            sparkReporter.config().setDocumentTitle("Mobile Automation Test Report");
            sparkReporter.config().setReportName(ConfigManager.getProperty("report.title", "Mobile Test Report"));
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // System information
            extent.setSystemInfo("Platform", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Test Platform", ConfigManager.getProperty("test.platform", "Unknown"));
            extent.setSystemInfo("Device Name", getDeviceName());
            extent.setSystemInfo("Platform Version", getPlatformVersion());

            // Jenkins information if available
            addJenkinsInfo();

            logger.info("Extent Report initialized at: {}", reportPath);
        }
        return extent;
    }

    private static String getReportPath() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportDir = "test-output/reports";
        new File(reportDir).mkdirs();
        return reportDir + "/ExtentReport_" + timestamp + ".html";
    }

    private static Theme getTheme() {
        String themeConfig = ConfigManager.getProperty("report.theme", "standard");
        return "dark".equalsIgnoreCase(themeConfig) ? Theme.DARK : Theme.STANDARD;
    }

    private static String getDeviceName() {
        String platform = ConfigManager.getProperty("test.platform", "unknown");
        return "android".equalsIgnoreCase(platform)
                ? ConfigManager.getProperty("android.device.name", "Unknown Android Device")
                : ConfigManager.getProperty("ios.device.name", "Unknown iOS Device");
    }

    private static String getPlatformVersion() {
        String platform = ConfigManager.getProperty("test.platform", "unknown");
        return "android".equalsIgnoreCase(platform)
                ? ConfigManager.getProperty("android.platform.version", "Unknown")
                : ConfigManager.getProperty("ios.platform.version", "Unknown");
    }

    private static void addJenkinsInfo() {
        String buildUrl = ConfigManager.getProperty("jenkins.build.url");
        String buildNumber = ConfigManager.getProperty("jenkins.build.number");
        String jobName = ConfigManager.getProperty("jenkins.job.name");

        if (buildUrl != null) extent.setSystemInfo("Build URL", buildUrl);
        if (buildNumber != null) extent.setSystemInfo("Build Number", buildNumber);
        if (jobName != null) extent.setSystemInfo("Job Name", jobName);
    }

    public static synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = createInstance().createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }

    public static synchronized ExtentTest getTest() {
        return test.get();
    }

    public static void logInfo(String message) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.log(Status.INFO, message);
        }
    }

    public static void logPass(String message) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.log(Status.PASS, message);
            if (ConfigManager.getBooleanProperty("screenshot.on.pass", false)) {
                attachScreenshot();
            }
        }
    }

    public static void logFail(String message) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.log(Status.FAIL, message);
            if (ConfigManager.getBooleanProperty("screenshot.on.failure", true)) {
                attachScreenshot();
            }
        }
    }

    public static void logSkip(String message) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.log(Status.SKIP, message);
        }
    }

    public static void attachScreenshot() {
        try {
            ExtentTest currentTest = getTest();
            if (currentTest != null) {
                String screenshotPath = ScreenshotUtils.captureScreenshot();
                if (screenshotPath != null) {
                    currentTest.addScreenCaptureFromPath(screenshotPath);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot", e);
        }
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            logger.info("Extent Report flushed successfully");
        }
    }

    public static void endTest() {
        test.remove();
    }
}