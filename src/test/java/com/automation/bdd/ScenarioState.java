package com.automation.bdd;

import com.automation.data.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * State shared between step definition classes within a single scenario.
 * PicoContainer creates a new instance per scenario and injects it via constructors.
 */
public class ScenarioState {

    private final List<Product> productsInCart = new ArrayList<>();

    public void addProduct(Product product) {
        productsInCart.add(product);
    }

    public List<String> productNamesInCart() {
        return productsInCart.stream().map(Product::displayName).toList();
    }
}
