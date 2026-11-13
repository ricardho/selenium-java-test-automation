package com.automation.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.automation.bdd.ScenarioState;
import com.automation.data.Customer;
import com.automation.data.CustomerFactory;
import com.automation.driver.DriverManager;
import com.automation.pages.CheckoutCompletePage;
import com.automation.pages.CheckoutInformationPage;
import com.automation.pages.CheckoutOverviewPage;
import com.automation.pages.InventoryPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;

public class CheckoutSteps {

    private final ScenarioState state;
    private CheckoutInformationPage informationPage;
    private CheckoutOverviewPage overviewPage;
    private CheckoutCompletePage completePage;

    public CheckoutSteps(ScenarioState state) {
        this.state = state;
    }

    @When("I check out with valid customer information")
    public void iCheckOutWithValidCustomerInformation() {
        overviewPage = startCheckout().continueWith(CustomerFactory.random());
    }

    @When("I check out leaving {string} empty")
    public void iCheckOutLeavingFieldEmpty(String field) {
        informationPage = startCheckout().submitInvalid(customerWithout(field));
    }

    @Then("the order overview should list the selected products")
    public void theOrderOverviewShouldListTheSelectedProducts() {
        assertThat(overviewPage.itemNames()).containsExactlyInAnyOrderElementsOf(state.productNamesInCart());
    }

    @Then("the order total should equal the item total plus tax")
    public void theOrderTotalShouldEqualItemTotalPlusTax() {
        BigDecimal itemsSum = overviewPage.itemPrices().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(overviewPage.subtotal()).isEqualByComparingTo(itemsSum);
        assertThat(overviewPage.total()).isEqualByComparingTo(overviewPage.subtotal().add(overviewPage.tax()));
    }

    @When("I finish the order")
    public void iFinishTheOrder() {
        completePage = overviewPage.finish();
    }

    @Then("I should see the confirmation message {string}")
    public void iShouldSeeTheConfirmationMessage(String expectedMessage) {
        assertThat(completePage.confirmationMessage()).isEqualTo(expectedMessage);
    }

    @Then("I should see the checkout error {string}")
    public void iShouldSeeTheCheckoutError(String expectedError) {
        assertThat(informationPage.errorMessage()).isEqualTo(expectedError);
    }

    private CheckoutInformationPage startCheckout() {
        return new InventoryPage(DriverManager.get()).goToCart().checkout();
    }

    private static Customer customerWithout(String field) {
        Customer customer = CustomerFactory.random();
        return switch (field) {
            case "first name" -> customer.withFirstName("");
            case "last name" -> customer.withLastName("");
            case "postal code" -> customer.withPostalCode("");
            default -> throw new IllegalArgumentException("Unknown checkout field: " + field);
        };
    }
}
