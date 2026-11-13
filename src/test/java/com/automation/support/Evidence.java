package com.automation.support;

import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/** Attaches debugging evidence to the Allure report. */
public final class Evidence {

    private Evidence() {
    }

    public static byte[] screenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public static void attachFailureEvidence(WebDriver driver) {
        try {
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(screenshot(driver)), "png");
            Allure.addAttachment("Page URL", "text/plain", driver.getCurrentUrl());
            Allure.addAttachment("Page source", "text/html", driver.getPageSource(), "html");
        } catch (WebDriverException e) {
            // The browser may have crashed; never hide the original test failure.
            Allure.addAttachment("Evidence capture failed", "text/plain", String.valueOf(e.getMessage()));
        }
    }
}
