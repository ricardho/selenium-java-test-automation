package com.automation.tests.ui;

import com.automation.data.Users;
import com.automation.driver.DriverManager;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import com.automation.support.WebDriverExtension;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@Epic("Web UI")
@Tag("ui")
@ExtendWith(WebDriverExtension.class)
abstract class BaseUiTest {

    protected WebDriver driver() {
        return DriverManager.get();
    }

    protected LoginPage openLoginPage() {
        return new LoginPage(driver()).open();
    }

    protected InventoryPage loginAsStandardUser() {
        return openLoginPage().loginAs(Users.STANDARD);
    }
}
