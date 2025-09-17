package utils;


import driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots";

    static {
        createScreenshotDirectory();
    }

    private static void createScreenshotDirectory() {
        File directory = new File(SCREENSHOT_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
            logger.info("Screenshot directory created: {}", SCREENSHOT_DIR);
        }
    }

    public static String captureScreenshot() {
        return captureScreenshot(generateScreenshotName());
    }

    public static String captureScreenshot(String fileName) {
        try {
            if (!DriverManager.isDriverInitialized()) {
                logger.warn("Driver not initialized, cannot capture screenshot");
                return null;
            }

            TakesScreenshot takesScreenshot = (TakesScreenshot) DriverManager.getDriver();
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String screenshotPath = SCREENSHOT_DIR + File.separator + fileName + ".png";
            File destFile = new File(screenshotPath);

            FileUtils.copyFile(sourceFile, destFile);

            logger.info("Screenshot captured: {}", screenshotPath);
            return screenshotPath;

        } catch (IOException e) {
            logger.error("Failed to capture screenshot", e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while capturing screenshot", e);
            return null;
        }
    }

    private static String generateScreenshotName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
        String threadId = String.valueOf(Thread.currentThread().getId());
        return "screenshot_" + timestamp + "_thread_" + threadId;
    }

    public static String captureScreenshotForScenario(String scenarioName) {
        String sanitizedName = sanitizeFileName(scenarioName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String fileName = sanitizedName + "_" + timestamp;
        return captureScreenshot(fileName);
    }

    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public static void cleanupOldScreenshots(int daysToKeep) {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (screenshotDir.exists()) {
            File[] files = screenshotDir.listFiles();
            if (files != null) {
                long cutoff = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);
                int deletedCount = 0;

                for (File file : files) {
                    if (file.lastModified() < cutoff) {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                }

                if (deletedCount > 0) {
                    logger.info("Cleaned up {} old screenshot(s)", deletedCount);
                }
            }
        }
    }
}
