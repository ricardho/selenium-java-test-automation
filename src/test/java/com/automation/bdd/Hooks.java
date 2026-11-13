package com.automation.bdd;

import com.automation.driver.DriverManager;
import com.automation.support.evidence.EvidenceRecorder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Browser and evidence lifecycle for every scenario: a fresh browser per scenario,
 * evidence collected according to {@code evidence.mode}, browser always closed.
 */
public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        DriverManager.start();
        EvidenceRecorder.start(scenario.getName(), featureName(scenario));
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            boolean failed = scenario.isFailed();
            EvidenceRecorder.captureFinalScreenshot(failed).ifPresent(png ->
                    scenario.attach(png, "image/png", failed ? "Screenshot on failure" : "Final state"));
            EvidenceRecorder.finish(scenario.getStatus().name(), null)
                    .ifPresent(pdf -> attachPdf(scenario, pdf));
        } finally {
            DriverManager.quit();
        }
    }

    private static void attachPdf(Scenario scenario, Path pdf) {
        try {
            scenario.attach(Files.readAllBytes(pdf), "application/pdf", "Evidence PDF");
        } catch (IOException e) {
            scenario.log("Evidence PDF generated at " + pdf.toAbsolutePath());
        }
    }

    private static String featureName(Scenario scenario) {
        String path = scenario.getUri().getSchemeSpecificPart();
        String file = path.substring(path.lastIndexOf('/') + 1);
        return file.replace(".feature", "");
    }
}
