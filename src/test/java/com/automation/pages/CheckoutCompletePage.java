package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutCompletePage extends BasePage {

    private static final By COMPLETE_HEADER = dataTest("complete-header");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        waitForTitle("Checkout: Complete!");
    }

    public String confirmationMessage() {
        return textOf(COMPLETE_HEADER);
    }
}
