package com.automation.data;

import java.util.Arrays;

public enum Product {
    BACKPACK("Sauce Labs Backpack"),
    BIKE_LIGHT("Sauce Labs Bike Light"),
    BOLT_T_SHIRT("Sauce Labs Bolt T-Shirt"),
    FLEECE_JACKET("Sauce Labs Fleece Jacket"),
    ONESIE("Sauce Labs Onesie"),
    RED_T_SHIRT("Test.allTheThings() T-Shirt (Red)");

    private final String displayName;

    Product(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static Product fromName(String name) {
        return Arrays.stream(values())
                .filter(product -> product.displayName.equals(name.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + name));
    }

    @Override
    public String toString() {
        return displayName;
    }
}
