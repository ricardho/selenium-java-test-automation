package com.automation.support;

import com.automation.config.TestConfig;
import com.automation.driver.DriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that owns the browser lifecycle:
 * opens a fresh browser per test, captures evidence on failure and always closes it.
 */
public class WebDriverExtension implements BeforeEachCallback, AfterTestExecutionCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        Allure.parameter("browser", TestConfig.browser().name().toLowerCase());
        DriverManager.start();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent() && DriverManager.isStarted()) {
            Evidence.attachFailureEvidence(DriverManager.get());
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        DriverManager.quit();
    }
}
