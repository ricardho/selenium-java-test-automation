package com.automation.driver;

import com.automation.config.TestConfig;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates WebDriver instances for local browsers or a Selenium Grid.
 * Drivers are resolved automatically by Selenium Manager (no WebDriverManager needed).
 */
public final class DriverFactory {

    /** Disables Chrome/Edge password-manager pop-ups that block the demo site login. */
    private static final Map<String, Object> CHROMIUM_PREFS = Map.of(
            "credentials_enable_service", false,
            "profile.password_manager_enabled", false,
            "profile.password_manager_leak_detection", false);

    private DriverFactory() {
    }

    public static WebDriver create() {
        BrowserType browser = TestConfig.browser();
        boolean headless = TestConfig.headless();
        return TestConfig.gridUrl()
                .<WebDriver>map(url -> new RemoteWebDriver(toUrl(url), optionsFor(browser, headless)))
                .orElseGet(() -> createLocal(browser, headless));
    }

    private static WebDriver createLocal(BrowserType browser, boolean headless) {
        return switch (browser) {
            case CHROME -> new ChromeDriver(chromeOptions(headless));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(headless));
            case EDGE -> new EdgeDriver(edgeOptions(headless));
        };
    }

    private static AbstractDriverOptions<?> optionsFor(BrowserType browser, boolean headless) {
        return switch (browser) {
            case CHROME -> chromeOptions(headless);
            case FIREFOX -> firefoxOptions(headless);
            case EDGE -> edgeOptions(headless);
        };
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        return configureChromium(new ChromeOptions(), headless);
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        return configureChromium(new EdgeOptions(), headless);
    }

    private static <T extends ChromiumOptions<?>> T configureChromium(T options, boolean headless) {
        options.addArguments("--window-size=1920,1080", "--disable-search-engine-choice-screen", "--disable-dev-shm-usage");
        options.setExperimentalOption("prefs", CHROMIUM_PREFS);
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920", "--height=1080");
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static URL toUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid grid.url: " + url, e);
        }
    }
}
