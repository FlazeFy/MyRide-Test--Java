package core;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {
    private static final Logger log = LogManager.getLogger(TestListener.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final String REPORT_DIR = System.getProperty("user.dir") + "/reports/";
    private static final String SCREENSHOT_DIR = REPORT_DIR + "screenshots/";

    @Override
    public void onStart(ITestContext context) {
        log.info("========== Test Suite Started: {} ==========", context.getName());

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String reportPath = REPORT_DIR + "extent-report_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setReportName("Report Automation MyRide");
        sparkReporter.config().setDocumentTitle("MyRide Web Apps Testing");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setEncoding("utf-8");
        sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", System.getProperty("env", "staging"));

        new File(SCREENSHOT_DIR).mkdirs();

        log.info("ExtentReports initialized. Report will be saved to: {}", reportPath);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String testDescription = result.getMethod().getDescription();

        log.info("---------- Test Started: {} ----------", testName);

        ExtentTest test = extent.createTest(testName, testDescription);
        test.assignCategory(result.getMethod().getGroups());
        extentTest.set(test);

        extentTest.get().log(Status.INFO, "Test execution started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();

        log.info("Test PASSED: {} (Duration: {}ms)", testName, duration);

        extentTest.get().log(Status.PASS, "Test executed successfully");
        extentTest.get().log(Status.INFO, "Execution time: " + duration + "ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        log.error("Test FAILED: {}", testName);
        log.error("Failure reason: {}", throwable != null ? throwable.getMessage() : "Unknown");

        extentTest.get().log(Status.FAIL, "Test failed: " + (throwable != null ? throwable.getMessage() : "Unknown error"));

        String screenshotPath = captureScreenshot(testName);
        if (screenshotPath != null) {
            try {
                extentTest.get().addScreenCaptureFromPath(screenshotPath, "Screenshot on failure");
                log.info("Screenshot captured: {}", screenshotPath);
            } catch (Exception e) {
                log.error("Failed to attach screenshot to report: {}", e.getMessage());
            }
        }

        if (throwable != null) {
            extentTest.get().log(Status.FAIL, throwable);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        log.warn("Test SKIPPED: {}", testName);

        ExtentTest test = extent.createTest(testName, result.getMethod().getDescription());
        extentTest.set(test);

        extentTest.get().log(Status.SKIP, "Test skipped");
        if (throwable != null) {
            extentTest.get().log(Status.SKIP, "Skip reason: " + throwable.getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("========== Test Suite Finished: {} ==========", context.getName());
        log.info("Total tests: {}, Passed: {}, Failed: {}, Skipped: {}",
                context.getAllTestMethods().length,
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());

        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed successfully");
        }
    }

    private String captureScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            log.warn("WebDriver is null, cannot capture screenshot");
            return null;
        }

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + fileName;
            String relativePath = "screenshots/" + fileName;

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);
            FileUtils.copyFile(srcFile, destFile);

            log.info("Screenshot saved: {}", filePath);
            return relativePath;
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return null;
        }
    }
}

