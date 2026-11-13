package com.automation.bdd;

import com.automation.driver.DriverManager;
import com.automation.support.Evidence;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriverException;

/** Browser lifecycle for every scenario: one fresh browser per scenario, evidence on failure. */
public class Hooks {

    @Before
    public void startBrowser() {
        DriverManager.start();
    }

    @After
    public void closeBrowser(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverManager.isStarted()) {
                scenario.attach(Evidence.screenshot(DriverManager.get()), "image/png", "Screenshot on failure");
            }
        } catch (WebDriverException e) {
            scenario.log("Could not capture screenshot: " + e.getMessage());
        } finally {
            DriverManager.quit();
        }
    }
}
