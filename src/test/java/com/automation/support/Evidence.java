package com.automation.support;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/** Low-level helpers to capture evidence from the browser. */
public final class Evidence {

    private Evidence() {
    }

    public static byte[] screenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /** Attaches the current URL and page source to Allure, to help debug failures. */
    public static void attachPageDetails(WebDriver driver) {
        try {
            Allure.addAttachment("Page URL", "text/plain", driver.getCurrentUrl());
            Allure.addAttachment("Page source", "text/html", driver.getPageSource(), "html");
        } catch (WebDriverException e) {
            // The browser may have crashed; never hide the original test failure.
            Allure.addAttachment("Page details unavailable", "text/plain", String.valueOf(e.getMessage()));
        }
    }
}
