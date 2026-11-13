package com.automation.pages;

import com.automation.data.Product;
import com.automation.data.SortOption;
import com.automation.pages.components.HeaderComponent;
import com.automation.support.Money;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

    public static final String TITLE = "Products";

    private static final By ITEM = dataTest("inventory-item");
    private static final By ITEM_NAME = dataTest("inventory-item-name");
    private static final By ITEM_PRICE = dataTest("inventory-item-price");
    private static final By SORT_SELECT = dataTest("product-sort-container");
    private static final By ADD_BUTTON = By.cssSelector("button[data-test^='add-to-cart']");
    private static final By REMOVE_BUTTON = By.cssSelector("button[data-test^='remove']");

    private final HeaderComponent header;

    public InventoryPage(WebDriver driver) {
        super(driver);
        waitForTitle(TITLE);
        this.header = new HeaderComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    @Step("Add '{product}' to the cart")
    public InventoryPage addToCart(Product product) {
        itemCard(product).findElement(ADD_BUTTON).click();
        return this;
    }

    @Step("Remove '{product}' from the cart")
    public InventoryPage removeFromCart(Product product) {
        itemCard(product).findElement(REMOVE_BUTTON).click();
        return this;
    }

    @Step("Sort products by {option}")
    public InventoryPage sortBy(SortOption option) {
        new Select(visible(SORT_SELECT)).selectByValue(option.value());
        return this;
    }

    public List<String> productNames() {
        return textsOf(ITEM_NAME);
    }

    public List<BigDecimal> productPrices() {
        return textsOf(ITEM_PRICE).stream().map(Money::parse).toList();
    }

    public CartPage goToCart() {
        return header.openCart();
    }

    private WebElement itemCard(Product product) {
        return wait.until(d -> d.findElements(ITEM).stream()
                .filter(card -> card.findElement(ITEM_NAME).getText().equals(product.displayName()))
                .findFirst()
                .orElse(null));
    }
}
