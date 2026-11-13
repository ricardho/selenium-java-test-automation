package com.automation.bdd.steps;

import com.automation.bdd.ScenarioState;
import com.automation.data.Product;
import com.automation.data.Users;
import com.automation.driver.DriverManager;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.List;

public class CartSteps {

    private final ScenarioState state;

    public CartSteps(ScenarioState state) {
        this.state = state;
    }

    @Given("I am logged in as a standard user")
    public void iAmLoggedInAsAStandardUser() {
        new LoginPage(DriverManager.get()).open().loginAs(Users.STANDARD);
    }

    @When("I add the following products to the cart:")
    public void iAddTheFollowingProductsToTheCart(List<String> productNames) {
        InventoryPage inventory = new InventoryPage(DriverManager.get());
        productNames.stream().map(Product::fromName).forEach(product -> {
            inventory.addToCart(product);
            state.addProduct(product);
        });
    }
}
