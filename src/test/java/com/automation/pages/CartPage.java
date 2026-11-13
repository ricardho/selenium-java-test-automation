package com.automation.pages;

import io.qameta.allure.Step;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private static final By ITEM_NAME = dataTest("inventory-item-name");
    private static final By CHECKOUT_BUTTON = dataTest("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
        waitForTitle("Your Cart");
    }

    public List<String> itemNames() {
        return textsOf(ITEM_NAME);
    }

    @Step("Proceed to checkout")
    public CheckoutInformationPage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutInformationPage(driver);
    }
}
