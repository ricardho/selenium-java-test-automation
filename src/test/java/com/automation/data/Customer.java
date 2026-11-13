package com.automation.data;

public record Customer(String firstName, String lastName, String postalCode) {

    public Customer withFirstName(String value) {
        return new Customer(value, lastName, postalCode);
    }

    public Customer withLastName(String value) {
        return new Customer(firstName, value, postalCode);
    }

    public Customer withPostalCode(String value) {
        return new Customer(firstName, lastName, value);
    }
}
