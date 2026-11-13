package com.automation.support.evidence;

import com.automation.driver.DriverManager;
import com.automation.support.Evidence;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that collects test evidence according to {@code evidence.mode}.
 *
 * <p>For UI tests register it AFTER {@code WebDriverExtension}: JUnit runs "after" callbacks in
 * reverse order, so evidence is captured while the browser is still open.
 */
public class EvidenceExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        EvidenceRecorder.start(context.getDisplayName(), context.getRequiredTestClass().getSimpleName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Throwable> failure = context.getExecutionException();
        boolean failed = failure.isPresent();

        EvidenceRecorder.captureFinalScreenshot(failed).ifPresent(png -> Allure.addAttachment(
                failed ? "Screenshot on failure" : "Final state", "image/png", new ByteArrayInputStream(png), "png"));
        if (failed && DriverManager.isStarted()) {
            Evidence.attachPageDetails(DriverManager.get());
        }

        EvidenceRecorder.finish(failed ? "FAILED" : "PASSED", failure.map(EvidenceRecorder::describeFailure).orElse(null))
                .ifPresent(EvidenceExtension::attachPdf);
    }

    private static void attachPdf(Path pdf) {
        try (InputStream content = Files.newInputStream(pdf)) {
            Allure.addAttachment("Evidence PDF", "application/pdf", content, "pdf");
        } catch (IOException e) {
            Allure.addAttachment("Evidence PDF", "text/plain", "Generated at " + pdf.toAbsolutePath());
        }
    }
}
