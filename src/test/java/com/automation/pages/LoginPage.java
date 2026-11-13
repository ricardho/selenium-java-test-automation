package com.automation.pages;

import com.automation.config.TestConfig;
import com.automation.data.User;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BaseComponent {

    private static final By USERNAME = dataTest("username");
    private static final By PASSWORD = dataTest("password");
    private static final By LOGIN_BUTTON = dataTest("login-button");
    private static final By ERROR_MESSAGE = dataTest("error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the login page")
    public LoginPage open() {
        driver.get(TestConfig.baseUrl());
        return waitUntilLoaded();
    }

    public LoginPage waitUntilLoaded() {
        visible(LOGIN_BUTTON);
        return this;
    }

    @Step("Log in as {user.username}")
    public InventoryPage loginAs(User user) {
        submit(user.username(), user.password());
        return new InventoryPage(driver);
    }

    /** Use for negative scenarios: stays on the login page. */
    @Step("Try to log in with username '{username}'")
    public LoginPage attemptLogin(String username, String password) {
        submit(username, password);
        return this;
    }

    public String errorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public boolean isLoaded() {
        return isDisplayed(LOGIN_BUTTON);
    }

    private void submit(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }
}
