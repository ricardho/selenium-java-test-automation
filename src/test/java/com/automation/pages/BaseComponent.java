package com.automation.pages;

import com.automation.config.TestConfig;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared interaction helpers for pages and components.
 * Every interaction goes through an explicit wait: no Thread.sleep, no implicit waits.
 */
public abstract class BaseComponent {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BaseComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TestConfig.timeout());
    }

    /** Locates elements by the {@code data-test} attribute, the most stable locator on the site. */
    protected static By dataTest(String value) {
        return By.cssSelector("[data-test='" + value + "']");
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        if (!text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected String textOf(By locator) {
        return visible(locator).getText();
    }

    protected List<String> textsOf(By locator) {
        return driver.findElements(locator).stream().map(WebElement::getText).toList();
    }

    protected boolean isDisplayed(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }
}
