package com.automation.tests.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.automation.data.Product;
import com.automation.data.SortOption;
import com.automation.pages.InventoryPage;
import io.qameta.allure.Feature;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Inventory")
@DisplayName("Inventory")
class InventoryTest extends BaseUiTest {

    @Test
    @DisplayName("Should sort products by price, low to high")
    void shouldSortByPriceLowToHigh() {
        List<BigDecimal> prices = loginAsStandardUser()
                .sortBy(SortOption.PRICE_LOW_TO_HIGH)
                .productPrices();

        assertThat(prices).hasSizeGreaterThan(1).isSorted();
    }

    @Test
    @DisplayName("Should sort products by name, Z to A")
    void shouldSortByNameDescending() {
        List<String> names = loginAsStandardUser()
                .sortBy(SortOption.NAME_Z_TO_A)
                .productNames();

        assertThat(names).hasSizeGreaterThan(1).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER.reversed());
    }

    @Test
    @DisplayName("Should update the cart badge when adding and removing products")
    void shouldUpdateCartBadge() {
        InventoryPage inventory = loginAsStandardUser()
                .addToCart(Product.BACKPACK)
                .addToCart(Product.BIKE_LIGHT);

        assertThat(inventory.header().cartItemCount()).isEqualTo(2);

        inventory.removeFromCart(Product.BACKPACK);

        assertThat(inventory.header().cartItemCount()).isEqualTo(1);
    }
}
