package com.automation.pages;

import com.automation.data.Customer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutInformationPage extends BasePage {

    private static final By FIRST_NAME = dataTest("firstName");
    private static final By LAST_NAME = dataTest("lastName");
    private static final By POSTAL_CODE = dataTest("postalCode");
    private static final By CONTINUE_BUTTON = dataTest("continue");
    private static final By ERROR_MESSAGE = dataTest("error");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
        waitForTitle("Checkout: Your Information");
    }

    @Step("Fill in customer information and continue")
    public CheckoutOverviewPage continueWith(Customer customer) {
        fillAndSubmit(customer);
        return new CheckoutOverviewPage(driver);
    }

    /** Use for validation scenarios: stays on the information page. */
    @Step("Submit incomplete customer information")
    public CheckoutInformationPage submitInvalid(Customer customer) {
        fillAndSubmit(customer);
        return this;
    }

    public String errorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    private void fillAndSubmit(Customer customer) {
        type(FIRST_NAME, customer.firstName());
        type(LAST_NAME, customer.lastName());
        type(POSTAL_CODE, customer.postalCode());
        click(CONTINUE_BUTTON);
    }
}
