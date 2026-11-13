package com.automation.api;

import com.automation.api.model.Booking;
import com.automation.api.model.BookingDates;
import java.time.LocalDate;
import net.datafaker.Faker;

public final class BookingFactory {

    private BookingFactory() {
    }

    public static Booking random() {
        Faker faker = new Faker();
        LocalDate checkin = LocalDate.now().plusDays(faker.number().numberBetween(1, 60));
        LocalDate checkout = checkin.plusDays(faker.number().numberBetween(1, 14));
        return new Booking(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().numberBetween(50, 2000),
                faker.bool().bool(),
                new BookingDates(checkin.toString(), checkout.toString()),
                faker.options().option("Breakfast", "Late checkout", "Airport transfer"));
    }
}
