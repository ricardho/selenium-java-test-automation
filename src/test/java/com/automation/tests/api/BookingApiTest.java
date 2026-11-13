package com.automation.tests.api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.automation.api.BookingClient;
import com.automation.api.BookingFactory;
import com.automation.api.model.Booking;
import com.automation.api.model.CreatedBooking;
import com.automation.config.TestConfig;
import com.automation.support.evidence.EvidenceExtension;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Epic("API")
@Feature("Booking")
@Tag("api")
@DisplayName("Booking API")
@ExtendWith(EvidenceExtension.class)
class BookingApiTest {

    private final BookingClient client = new BookingClient();

    @Test
    @Tag("smoke")
    @DisplayName("Should create a booking")
    void shouldCreateBooking() {
        Booking booking = BookingFactory.random();

        CreatedBooking created = client.create(booking)
                .then().statusCode(200)
                .extract().as(CreatedBooking.class);

        assertThat(created.bookingid()).isPositive();
        assertThat(created.booking()).isEqualTo(booking);
    }

    @Test
    @DisplayName("Should return a booking that matches the JSON schema")
    void shouldMatchBookingSchema() {
        int bookingId = createBooking(BookingFactory.random());

        client.get(bookingId)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }

    @Test
    @DisplayName("Should update a booking when authenticated")
    void shouldUpdateBooking() {
        int bookingId = createBooking(BookingFactory.random());
        Booking changes = BookingFactory.random();

        Booking updated = client.update(bookingId, changes, client.authenticate())
                .then().statusCode(200)
                .extract().as(Booking.class);

        assertThat(updated).isEqualTo(changes);
    }

    @Test
    @DisplayName("Should not update a booking with an invalid token")
    void shouldNotUpdateWithInvalidToken() {
        int bookingId = createBooking(BookingFactory.random());

        client.update(bookingId, BookingFactory.random(), "invalid-token")
                .then().statusCode(403);
    }

    @Test
    @DisplayName("Should delete a booking")
    void shouldDeleteBooking() {
        int bookingId = createBooking(BookingFactory.random());

        // Restful Booker returns 201 for a successful delete (documented quirk).
        client.delete(bookingId, client.authenticate()).then().statusCode(201);
        client.get(bookingId).then().statusCode(404);
    }

    @Test
    @DisplayName("Should not issue a token for invalid credentials")
    void shouldRejectInvalidCredentials() {
        // The API answers 200 with an error body instead of 401 (documented quirk).
        client.createToken(TestConfig.apiUsername(), "wrong-password")
                .then().statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }

    private int createBooking(Booking booking) {
        return client.create(booking).then().statusCode(200).extract().path("bookingid");
    }
}
