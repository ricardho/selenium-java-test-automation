package com.automation.tests.ui;

import com.automation.data.Users;
import com.automation.driver.DriverManager;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import com.automation.support.WebDriverExtension;
import com.automation.support.evidence.EvidenceExtension;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@Epic("Web UI")
@Tag("ui")
// Order matters: EvidenceExtension "after" callbacks run first, while the browser is still open.
@ExtendWith({WebDriverExtension.class, EvidenceExtension.class})
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
