package listeners;

import reporting.ExtentReportManager;
import utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class RetryListener implements IAnnotationTransformer {
    private static final Logger logger = LoggerFactory.getLogger(RetryListener.class);

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    public static class RetryAnalyzer implements IRetryAnalyzer {
        private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
        private int retryCount = 0;
        private static final int MAX_RETRY_COUNT = ConfigManager.getIntProperty("test.retry.count", 2);

        @Override
        public boolean retry(ITestResult result) {
            if (retryCount < MAX_RETRY_COUNT) {
                retryCount++;
                String testName = result.getMethod().getMethodName();
                logger.warn("Retrying test: {} (attempt {}/{})", testName, retryCount, MAX_RETRY_COUNT);

                ExtentReportManager.logInfo("Retrying test: " + testName + " (attempt " + retryCount + "/" + MAX_RETRY_COUNT + ")");

                // Set the test result to skip for retry attempts
                result.setStatus(ITestResult.SKIP);

                return true;
            }
            return false;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public static int getMaxRetryCount() {
            return MAX_RETRY_COUNT;
        }
    }
}
