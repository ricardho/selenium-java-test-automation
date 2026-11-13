package com.automation.support;

import com.automation.config.TestConfig;
import com.automation.driver.DriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that owns the browser lifecycle: a fresh browser per test, always closed.
 * Evidence is handled separately by {@code EvidenceExtension}.
 */
public class WebDriverExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        Allure.parameter("browser", TestConfig.browser().name().toLowerCase());
        DriverManager.start();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        DriverManager.quit();
    }
}
