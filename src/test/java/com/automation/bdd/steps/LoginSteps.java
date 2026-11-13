package com.automation.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.automation.config.TestConfig;
import com.automation.data.Users;
import com.automation.driver.DriverManager;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    private static final String SAUCE_PASSWORD_PLACEHOLDER = "${SAUCE_PASSWORD}";

    private LoginPage loginPage;

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage = new LoginPage(DriverManager.get()).open();
    }

    @When("I log in as a standard user")
    public void iLogInAsAStandardUser() {
        loginPage.loginAs(Users.STANDARD);
    }

    @When("I try to log in with username {string} and password {string}")
    public void iTryToLogIn(String username, String password) {
        loginPage.attemptLogin(username, resolveSecret(password));
    }

    /** Replaces the ${SAUCE_PASSWORD} placeholder used in feature files with the configured secret. */
    private static String resolveSecret(String value) {
        return SAUCE_PASSWORD_PLACEHOLDER.equals(value) ? TestConfig.saucePassword() : value;
    }

    @Then("I should see the products page")
    public void iShouldSeeTheProductsPage() {
        assertThat(new InventoryPage(DriverManager.get()).pageTitle()).isEqualTo(InventoryPage.TITLE);
    }

    @Then("I should see the login error {string}")
    public void iShouldSeeTheLoginError(String expectedError) {
        assertThat(loginPage.errorMessage()).isEqualTo(expectedError);
    }
}
