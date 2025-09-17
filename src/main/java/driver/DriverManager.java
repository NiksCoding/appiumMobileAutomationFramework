package driver;



import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static void initializeDriver(String platform) {
        try {
            AppiumDriver appiumDriver = createDriver(platform);
            appiumDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
            driver.set(appiumDriver);
            logger.info("Driver initialized successfully for platform: {}", platform);
        } catch (Exception e) {
            logger.error("Failed to initialize driver for platform: {}", platform, e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }

    private static AppiumDriver createDriver(String platform) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String appiumServerUrl = ConfigManager.getProperty("appium.server.url");

        switch (platform.toLowerCase()) {
            case "android":
                return createAndroidDriver(capabilities, appiumServerUrl);
            case "ios":
                return createIOSDriver(capabilities, appiumServerUrl);
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    private static AndroidDriver createAndroidDriver(DesiredCapabilities capabilities, String serverUrl)
            throws MalformedURLException {
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", ConfigManager.getProperty("android.platform.version"));
        capabilities.setCapability("deviceName", ConfigManager.getProperty("android.device.name"));
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("app", ConfigManager.getProperty("android.app.path"));
        capabilities.setCapability("appPackage", ConfigManager.getProperty("android.app.package"));
        capabilities.setCapability("appActivity", ConfigManager.getProperty("android.app.activity"));
        capabilities.setCapability("noReset", false);
        capabilities.setCapability("fullReset", false);
        capabilities.setCapability("newCommandTimeout", 300);

        return new AndroidDriver(new URL(serverUrl), capabilities);
    }

    private static IOSDriver createIOSDriver(DesiredCapabilities capabilities, String serverUrl)
            throws MalformedURLException {
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("platformVersion", ConfigManager.getProperty("ios.platform.version"));
        capabilities.setCapability("deviceName", ConfigManager.getProperty("ios.device.name"));
        capabilities.setCapability("automationName", "XCUITest");
        capabilities.setCapability("app", ConfigManager.getProperty("ios.app.path"));
        capabilities.setCapability("bundleId", ConfigManager.getProperty("ios.bundle.id"));
        capabilities.setCapability("noReset", false);
        capabilities.setCapability("fullReset", false);
        capabilities.setCapability("newCommandTimeout", 300);
        capabilities.setCapability("wdaLocalPort", 8100);

        return new IOSDriver(new URL(serverUrl), capabilities);
    }

    public static AppiumDriver getDriver() {
        AppiumDriver currentDriver = driver.get();
        if (currentDriver == null) {
            throw new IllegalStateException("Driver is not initialized. Call initializeDriver() first.");
        }
        return currentDriver;
    }

    public static void quitDriver() {
        AppiumDriver currentDriver = driver.get();
        if (currentDriver != null) {
            currentDriver.quit();
            driver.remove();
            logger.info("Driver quit successfully");
        }
    }

    public static boolean isDriverInitialized() {
        return driver.get() != null;
    }
}