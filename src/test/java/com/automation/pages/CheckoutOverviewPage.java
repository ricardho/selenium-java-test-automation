package com.automation.pages;

import com.automation.support.Money;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutOverviewPage extends BasePage {

    private static final By ITEM_NAME = dataTest("inventory-item-name");
    private static final By ITEM_PRICE = dataTest("inventory-item-price");
    private static final By SUBTOTAL = dataTest("subtotal-label");
    private static final By TAX = dataTest("tax-label");
    private static final By TOTAL = dataTest("total-label");
    private static final By FINISH_BUTTON = dataTest("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
        waitForTitle("Checkout: Overview");
    }

    public List<String> itemNames() {
        return textsOf(ITEM_NAME);
    }

    public List<BigDecimal> itemPrices() {
        return textsOf(ITEM_PRICE).stream().map(Money::parse).toList();
    }

    public BigDecimal subtotal() {
        return Money.parse(textOf(SUBTOTAL));
    }

    public BigDecimal tax() {
        return Money.parse(textOf(TAX));
    }

    public BigDecimal total() {
        return Money.parse(textOf(TOTAL));
    }

    @Step("Finish the order")
    public CheckoutCompletePage finish() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver);
    }
}
