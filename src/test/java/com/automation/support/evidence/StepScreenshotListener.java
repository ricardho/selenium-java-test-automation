package com.automation.support.evidence;

import com.automation.config.TestConfig;
import com.automation.driver.DriverManager;
import com.automation.support.Evidence;
import io.qameta.allure.Allure;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.WebDriverException;

/**
 * In {@link EvidenceMode#STEPS} mode, takes a screenshot at the end of every step and attaches it
 * both to the Allure step and to the PDF evidence.
 *
 * <p>Works for page-object {@code @Step} methods and Cucumber steps alike, because both are reported
 * as Allure steps. Only leaf steps are captured, to avoid duplicate images of the same screen.
 * Registered through {@code META-INF/services}.
 */
public class StepScreenshotListener implements StepLifecycleListener {

    @Override
    public void beforeStepStop(StepResult step) {
        if (TestConfig.evidenceMode() != EvidenceMode.STEPS
                || !step.getSteps().isEmpty()
                || !EvidenceRecorder.isActive()
                || !DriverManager.isStarted()) {
            return;
        }
        try {
            byte[] png = Evidence.screenshot(DriverManager.get());
            Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png", png);
            EvidenceRecorder.addScreenshot(step.getName(), png);
        } catch (WebDriverException e) {
            // A screenshot failure must never fail the step itself.
        }
    }
}
