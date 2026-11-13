package com.automation.data;

import java.util.Locale;
import net.datafaker.Faker;

/** Generates random, valid checkout customers so tests never depend on fixed data. */
public final class CustomerFactory {

    private CustomerFactory() {
    }

    public static Customer random() {
        Faker faker = new Faker(Locale.of("pt", "BR"));
        return new Customer(faker.name().firstName(), faker.name().lastName(), faker.address().zipCode());
    }
}
