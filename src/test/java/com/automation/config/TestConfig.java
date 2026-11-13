package com.automation.config;

import com.automation.driver.BrowserType;
import com.automation.support.evidence.EvidenceMode;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Single entry point for test configuration.
 *
 * <p>Resolution order for every key (first match wins):
 * <ol>
 *   <li>JVM system property: {@code -Dbase.url=...}</li>
 *   <li>Environment variable: {@code BASE_URL=...} (dots become underscores, upper case)</li>
 *   <li>Local {@code .env} file in the project root (git-ignored, for secrets on a developer machine)</li>
 *   <li>{@code config.properties} on the test classpath (non-sensitive settings only)</li>
 *   <li>Hard-coded default</li>
 * </ol>
 *
 * <p>Secrets never live in versioned files: locally they come from {@code .env},
 * in CI from the pipeline's secret store exposed as environment variables.
 */
public final class TestConfig {

    private static final Properties FILE_PROPERTIES = loadFile();
    private static final Map<String, String> DOT_ENV = loadDotEnv(Path.of(".env"));

    private TestConfig() {
    }

    public static String baseUrl() {
        return get("base.url", "https://www.saucedemo.com");
    }

    public static String apiBaseUrl() {
        return get("api.base.url", "https://restful-booker.herokuapp.com");
    }

    public static BrowserType browser() {
        return BrowserType.from(get("browser", "chrome"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    /** Selenium Grid URL. When empty, browsers run locally. */
    public static Optional<String> gridUrl() {
        return Optional.ofNullable(get("grid.url", null)).filter(TestConfig::isSet);
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds", "10")));
    }

    public static EvidenceMode evidenceMode() {
        return EvidenceMode.from(get("evidence.mode", "failure"));
    }

    public static boolean evidencePdf() {
        return Boolean.parseBoolean(get("evidence.pdf", "true"));
    }

    public static Path evidenceDirectory() {
        return Path.of(get("evidence.dir", "target/evidence"));
    }

    public static String saucePassword() {
        return required("sauce.password");
    }

    public static String apiUsername() {
        return required("api.username");
    }

    public static String apiPassword() {
        return required("api.password");
    }

    public static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (isSet(systemValue)) {
            return systemValue;
        }
        String envName = toEnvName(key);
        String envValue = System.getenv(envName);
        if (isSet(envValue)) {
            return envValue;
        }
        String dotEnvValue = DOT_ENV.get(envName);
        if (isSet(dotEnvValue)) {
            return dotEnvValue;
        }
        return FILE_PROPERTIES.getProperty(key, defaultValue);
    }

    private static String required(String key) {
        String value = get(key, null);
        if (!isSet(value)) {
            throw new IllegalStateException(
                    "Missing configuration '%s'. Set the %s environment variable, add it to a local .env file "
                            .formatted(key, toEnvName(key))
                            + "(see .env.example) or pass -D" + key + "=...");
        }
        return value;
    }

    private static String toEnvName(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }

    private static Properties loadFile() {
        Properties properties = new Properties();
        try (InputStream in = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read config.properties", e);
        }
        return properties;
    }

    /** Minimal .env parser: KEY=VALUE lines, '#' comments, optional 'export ' prefix and quotes. */
    private static Map<String, String> loadDotEnv(Path file) {
        if (!Files.isRegularFile(file)) {
            return Map.of();
        }
        Map<String, String> values = new HashMap<>();
        try {
            for (String rawLine : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String line = rawLine.strip();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("export ")) {
                    line = line.substring("export ".length()).strip();
                }
                int separator = line.indexOf('=');
                if (separator <= 0) {
                    continue;
                }
                values.put(line.substring(0, separator).strip(), unquote(line.substring(separator + 1).strip()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + file.toAbsolutePath(), e);
        }
        return Map.copyOf(values);
    }

    private static String unquote(String value) {
        boolean quoted = value.length() >= 2
                && (value.startsWith("\"") && value.endsWith("\"") || value.startsWith("'") && value.endsWith("'"));
        return quoted ? value.substring(1, value.length() - 1) : value;
    }
}
