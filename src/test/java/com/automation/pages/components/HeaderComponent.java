package com.automation.pages.components;

import com.automation.pages.BaseComponent;
import com.automation.pages.CartPage;
import com.automation.pages.LoginPage;
import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Header shared by every page after login: cart link, cart badge and side menu. */
public class HeaderComponent extends BaseComponent {

    private static final By CART_LINK = By.className("shopping_cart_link");
    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");
    private static final Duration MENU_ANIMATION_TIMEOUT = Duration.ofSeconds(3);

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    public int cartItemCount() {
        return driver.findElements(CART_BADGE).stream()
                .findFirst()
                .map(WebElement::getText)
                .map(Integer::parseInt)
                .orElse(0);
    }

    @Step("Open the cart")
    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }

    @Step("Log out")
    public LoginPage logout() {
        openMenu();
        click(LOGOUT_LINK);
        return new LoginPage(driver).waitUntilLoaded();
    }

    /**
     * Under heavy load (parallel browsers) the slide-in animation occasionally swallows the
     * first click. Retry the click once instead of adding sleeps or longer timeouts.
     */
    private void openMenu() {
        click(MENU_BUTTON);
        try {
            new WebDriverWait(driver, MENU_ANIMATION_TIMEOUT)
                    .until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_LINK));
        } catch (TimeoutException firstAttemptFailed) {
            click(MENU_BUTTON);
            visible(LOGOUT_LINK);
        }
    }
}
