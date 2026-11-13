package com.automation.support.evidence;

import com.automation.config.TestConfig;
import com.automation.driver.DriverManager;
import com.automation.support.Evidence;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.openqa.selenium.WebDriverException;

/**
 * Collects the evidence of the test running on the current thread (screenshots, HTTP exchanges)
 * and turns it into a PDF document when the test finishes.
 *
 * <p>Framework-agnostic: driven by {@link EvidenceExtension} for JUnit and by the Cucumber hooks.
 * One recorder per thread, so it is safe with parallel execution.
 */
public final class EvidenceRecorder {

    private static final ThreadLocal<TestEvidence> CURRENT = new ThreadLocal<>();

    private EvidenceRecorder() {
    }

    /** Starts recording. Call after the browser (if any) has been started. */
    public static void start(String testName, String suite) {
        CURRENT.set(new TestEvidence(testName, suite, LocalDateTime.now(), describeExecution(), new ArrayList<>()));
    }

    public static boolean isActive() {
        return CURRENT.get() != null;
    }

    public static void addScreenshot(String title, byte[] png) {
        current().ifPresent(evidence -> evidence.items().add(EvidenceItem.screenshot(title, png)));
    }

    public static void addText(String title, String text) {
        current().ifPresent(evidence -> evidence.items().add(EvidenceItem.text(title, text)));
    }

    /**
     * Takes the closing screenshot when the configured mode asks for it
     * (always on failure; on success only in ALWAYS/STEPS modes).
     *
     * @return the screenshot, so the caller can also attach it to its own report
     */
    public static Optional<byte[]> captureFinalScreenshot(boolean failed) {
        boolean wanted = failed || TestConfig.evidenceMode().capturesPassedTests();
        if (!wanted || !DriverManager.isStarted()) {
            return Optional.empty();
        }
        try {
            byte[] png = Evidence.screenshot(DriverManager.get());
            addScreenshot(failed ? "Screen at the moment of failure" : "Final state", png);
            return Optional.of(png);
        } catch (WebDriverException e) {
            return Optional.empty(); // never hide the real test result because of evidence
        }
    }

    /**
     * Stops recording and writes the PDF when enabled.
     * In FAILURE mode only failed tests produce a document.
     *
     * @return the PDF path, so the caller can attach it to its report
     */
    public static Optional<Path> finish(String status, String failureMessage) {
        TestEvidence evidence = CURRENT.get();
        CURRENT.remove();
        if (evidence == null || !TestConfig.evidencePdf()) {
            return Optional.empty();
        }
        boolean failed = "FAILED".equalsIgnoreCase(status);
        if (!failed && !TestConfig.evidenceMode().capturesPassedTests()) {
            return Optional.empty();
        }
        Duration duration = Duration.between(evidence.startedAt(), LocalDateTime.now());
        return EvidencePdfWriter.write(evidence, status, failureMessage, duration, TestConfig.evidenceDirectory());
    }

    /**
     * Human-readable failure summary. Selenium exceptions append build, driver and session
     * details (including local paths) that add noise to an evidence document, so they are cut.
     */
    public static String describeFailure(Throwable failure) {
        String message = String.valueOf(failure.getMessage());
        int seleniumDetails = message.indexOf("\nBuild info:");
        if (seleniumDetails >= 0) {
            message = message.substring(0, seleniumDetails);
        }
        return failure.getClass().getSimpleName() + ": " + message.strip();
    }

    private static Optional<TestEvidence> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    private static Map<String, String> describeExecution() {
        Map<String, String> details = new LinkedHashMap<>();
        if (DriverManager.isStarted()) {
            details.put("Application", TestConfig.baseUrl());
            details.put("Browser", TestConfig.browser().name().toLowerCase()
                    + (TestConfig.headless() ? " (headless)" : ""));
            details.put("Execution", TestConfig.gridUrl().map(url -> "Selenium Grid").orElse("Local"));
        } else {
            details.put("API", TestConfig.apiBaseUrl());
        }
        details.put("Evidence mode", TestConfig.evidenceMode().name().toLowerCase());
        details.put("Platform", System.getProperty("os.name") + " / Java " + System.getProperty("java.version"));
        return details;
    }

    record TestEvidence(String testName, String suite, LocalDateTime startedAt,
                        Map<String, String> execution, List<EvidenceItem> items) {
    }

    record EvidenceItem(String title, byte[] png, String text) {

        static EvidenceItem screenshot(String title, byte[] png) {
            return new EvidenceItem(title, png, null);
        }

        static EvidenceItem text(String title, String text) {
            return new EvidenceItem(title, null, text);
        }
    }
}
