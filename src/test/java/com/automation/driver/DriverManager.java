package com.automation.driver;

import org.openqa.selenium.WebDriver;

/**
 * Holds one WebDriver per thread, which makes parallel execution safe
 * for both JUnit 5 and Cucumber.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void start() {
        if (DRIVER.get() == null) {
            DRIVER.set(DriverFactory.create());
        }
    }

    public static WebDriver get() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver was not started for thread " + Thread.currentThread().getName());
        }
        return driver;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
