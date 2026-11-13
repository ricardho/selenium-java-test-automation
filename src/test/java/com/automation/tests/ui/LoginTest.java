package com.automation.tests.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.automation.config.TestConfig;
import com.automation.data.Users;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Feature("Login")
@DisplayName("Login")
class LoginTest extends BaseUiTest {

    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Should log in with valid credentials")
    void shouldLoginWithValidCredentials() {
        InventoryPage inventory = loginAsStandardUser();

        assertThat(inventory.pageTitle()).isEqualTo(InventoryPage.TITLE);
    }

    @ParameterizedTest(name = "Should reject login: {0}")
    @MethodSource("invalidLogins")
    @Severity(SeverityLevel.CRITICAL)
    void shouldRejectInvalidLogin(String scenario, String username, String password, String expectedError) {
        LoginPage loginPage = openLoginPage().attemptLogin(username, password);

        assertThat(loginPage.errorMessage()).isEqualTo(expectedError);
    }

    static Stream<Arguments> invalidLogins() {
        String password = TestConfig.saucePassword();
        return Stream.of(
                Arguments.of("locked out user", Users.LOCKED_OUT.username(), password,
                        "Epic sadface: Sorry, this user has been locked out."),
                Arguments.of("wrong password", Users.STANDARD.username(), "wrong_password",
                        "Epic sadface: Username and password do not match any user in this service"),
                Arguments.of("empty username", "", password,
                        "Epic sadface: Username is required"),
                Arguments.of("empty password", Users.STANDARD.username(), "",
                        "Epic sadface: Password is required"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should log out and return to the login page")
    void shouldLogout() {
        LoginPage loginPage = loginAsStandardUser().header().logout();

        assertThat(loginPage.isLoaded()).isTrue();
    }
}
