package com.automation.tests.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.automation.data.Customer;
import com.automation.data.CustomerFactory;
import com.automation.data.Product;
import com.automation.pages.CheckoutCompletePage;
import com.automation.pages.CheckoutInformationPage;
import com.automation.pages.CheckoutOverviewPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Feature("Checkout")
@DisplayName("Checkout")
class CheckoutTest extends BaseUiTest {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");

    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Should complete a purchase")
    void shouldCompletePurchase() {
        CheckoutCompletePage complete = loginAsStandardUser()
                .addToCart(Product.BACKPACK)
                .goToCart()
                .checkout()
                .continueWith(CustomerFactory.random())
                .finish();

        assertThat(complete.confirmationMessage()).isEqualTo("Thank you for your order!");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should calculate item total, tax and order total")
    void shouldCalculateOrderTotal() {
        CheckoutOverviewPage overview = loginAsStandardUser()
                .addToCart(Product.BACKPACK)
                .addToCart(Product.FLEECE_JACKET)
                .goToCart()
                .checkout()
                .continueWith(CustomerFactory.random());

        BigDecimal itemsSum = overview.itemPrices().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expectedTax = itemsSum.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

        assertSoftly(softly -> {
            softly.assertThat(overview.itemNames())
                    .containsExactlyInAnyOrder(Product.BACKPACK.displayName(), Product.FLEECE_JACKET.displayName());
            softly.assertThat(overview.subtotal()).isEqualByComparingTo(itemsSum);
            softly.assertThat(overview.tax()).isEqualByComparingTo(expectedTax);
            softly.assertThat(overview.total()).isEqualByComparingTo(itemsSum.add(expectedTax));
        });
    }

    @ParameterizedTest(name = "Should require {0}")
    @MethodSource("incompleteCustomers")
    @Severity(SeverityLevel.NORMAL)
    void shouldRequireCustomerInformation(String field, Customer customer, String expectedError) {
        CheckoutInformationPage information = loginAsStandardUser()
                .addToCart(Product.ONESIE)
                .goToCart()
                .checkout()
                .submitInvalid(customer);

        assertThat(information.errorMessage()).isEqualTo(expectedError);
    }

    static Stream<Arguments> incompleteCustomers() {
        Customer valid = CustomerFactory.random();
        return Stream.of(
                Arguments.of("first name", valid.withFirstName(""), "Error: First Name is required"),
                Arguments.of("last name", valid.withLastName(""), "Error: Last Name is required"),
                Arguments.of("postal code", valid.withPostalCode(""), "Error: Postal Code is required"));
    }
}
