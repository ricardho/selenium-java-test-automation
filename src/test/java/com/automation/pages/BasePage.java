package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Base class for full pages. Pages verify they are loaded before being used. */
public abstract class BasePage extends BaseComponent {

    private static final By PAGE_TITLE = dataTest("title");

    protected BasePage(WebDriver driver) {
        super(driver);
    }

    protected void waitForTitle(String expectedTitle) {
        wait.until(ExpectedConditions.textToBe(PAGE_TITLE, expectedTitle));
    }

    public String pageTitle() {
        return textOf(PAGE_TITLE);
    }
}
